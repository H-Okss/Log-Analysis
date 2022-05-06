#in one terminal, start the log generator:
python3 log-generator.py
# in a 2nd terminal, watch the output from the logs:
# every 5 seconds, you should see new entries
tail -f /tmp/log-generator.log > /home/dba/Desktop/simple/simple/data/data`date +%Y%m%d%H%M%S`.csv

