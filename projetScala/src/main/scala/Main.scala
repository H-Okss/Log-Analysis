import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.streaming
import org.apache.spark.sql.functions._
import org.apache.log4j._
import org.apache.spark.SparkConf
import org.elasticsearch.hadoop.cfg.ConfigurationOptions
import org.elasticsearch.spark.sql._


object Main {
  def main(args: Array[String]): Unit = {
    // Création d'un objet SparkSession avec la configuration d'ElasticSearch
    val spark = SparkSession.builder()
      .config(ConfigurationOptions.ES_NODES, "127.0.0.1")
      .config(ConfigurationOptions.ES_PORT, "9200")
      .master("local[*]")
      .appName("projetScala")
      .getOrCreate()

    Logger.getLogger("org").setLevel(Level.ERROR)

    // Création du shéma du fichier log
    val schema = StructType(List(
      StructField("HTTP", StringType, true),
      StructField("HTTP_Status", IntegerType, true),
      StructField("URL", StringType, true),
      StructField("Path", StringType, true),
      StructField("IP", StringType, true)))

    //Lecture à partir du fichier des logs
    val StreamDF = spark.readStream.option("delimiter", " ").schema(schema)
    .csv("/home/dba/Desktop/projetScala/projetScala/data/data*.csv")
    StreamDF.createOrReplaceTempView("SDF")

    //Ecriture vers ElasticSearch
    val outDF = spark.sql("select * from SDF")
    outDF.writeStream
      .format("org.elasticsearch.spark.sql")
      .outputMode("append") //Le mode "append" est un mode de sortie où seulement les nouvelles lignes du streaming DataFrame seront écrites
      .option("es.port","9200")
      .option("es.nodes","localhost")
      .option("checkpointLocation", "checkpoint")
      .start("logstream/doc")
      .awaitTermination()
  }



}
