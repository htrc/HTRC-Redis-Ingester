#!/bin/bash

# creates a TSV file with the volume ids and access levels of volumes whose
# access levels have been updated since a specified timestamp; this TSV file
# is used as input to HTRC-Redis-Ingester to set access levels in a full corpus
# redis

HT_RIGHTS_DB_HOST="HT_RIGHTS_DB_HOST"
VOL_TSV_FILES_DIR="/home/htrcprod/redis-updater"
ACCESS_LEVEL_TSV_FILE="$VOL_TSV_FILES_DIR/modifiedAccessLevels.tsv"

RIGHTS_UPDATE_TIMESTAMP_FILE="/home/htrcprod/redis-updater/time-of-last-rights-update.txt"
LAST_UPDATE_TIME=`cat $RIGHTS_UPDATE_TIMESTAMP_FILE`

echo "--------------------------------------------------"
DATE=`date +%Y%m%d`
printf "Time of last update: $LAST_UPDATE_TIME\n\n"

if [ -z "$LAST_UPDATE_TIME" ]; then
    echo "Time of last update not set. Exiting."
    exit 1
fi

HTRC_REDIS_INGESTER_JAR="/home/htrcprod/redis-updater/HTRC-Redis-Ingester-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
REDIS_HOST="REDIS_HOST"
REDIS_PASSWORD="REDIS_PASSWORD"

HTRC_REDIS_INGESTER_LOG_DIR="/home/htrcprod/redis-updater/logs/htrc-redis-ingester.log"

# 1A
mysql -h $HT_RIGHTS_DB_HOST -u htrc ht -N --execute \
  "select concat(namespace, '.', id), '1A' from rights_current where \
   attr = 9 AND access_profile = 1 AND time > '$LAST_UPDATE_TIME' \
   order by concat(namespace, '.', id);" > "$ACCESS_LEVEL_TSV_FILE"

# 1B
mysql -h $HT_RIGHTS_DB_HOST -u htrc ht -N --execute \
  "select concat(namespace, '.', id), '1B' from rights_current where \
   attr in (1, 7, 10, 11, 12, 13, 14, 15, 17, 18, 20, 21, 22, 23, 24, 25) \
   AND access_profile = 1 AND time > '$LAST_UPDATE_TIME' \
   order by concat(namespace, '.', id);" >> "$ACCESS_LEVEL_TSV_FILE"

# 2A
mysql -h $HT_RIGHTS_DB_HOST -u htrc ht -N --execute \
  "select concat(namespace, '.', id), '2A' from rights_current where \
   attr = 9 AND access_profile = 2 AND time > '$LAST_UPDATE_TIME' \
   order by concat(namespace, '.', id);" >> "$ACCESS_LEVEL_TSV_FILE"

# 2B
mysql -h $HT_RIGHTS_DB_HOST -u htrc ht -N --execute \
  "select concat(namespace, '.', id), '2B' from rights_current where \
   attr in (1, 7, 10, 11, 12, 13, 14, 15, 17, 18, 20, 21, 22, 23, 24, 25) \
   AND access_profile = 2 AND time > '$LAST_UPDATE_TIME' \
   order by concat(namespace, '.', id);" >> "$ACCESS_LEVEL_TSV_FILE"

# 3
mysql -h $HT_RIGHTS_DB_HOST -u htrc ht -N --execute \
  "select concat(namespace, '.', id), '3' from rights_current where \
   attr in (2, 3, 4, 5, 6, 8, 16, 19, 26, 27) \
   AND time > '$LAST_UPDATE_TIME' \
   order by concat(namespace, '.', id);" >> "$ACCESS_LEVEL_TSV_FILE"

NUM_MOD_ACCESS_LEVELS=$(wc -l < "$ACCESS_LEVEL_TSV_FILE")
printf "No. of new access levels = $NUM_MOD_ACCESS_LEVELS\n"

# update the full corpus redis with the new access levels
java -jar "$HTRC_REDIS_INGESTER_JAR" -l "$ACCESS_LEVEL_TSV_FILE" -h "$REDIS_HOST" -p "$REDIS_PASSWORD"

printf "Set access levels in redis. See $HTRC_REDIS_INGESTER_LOG_DIR.\n"

# save the latest time seen in this update
NEW_TIME_OF_LAST_UPDATE=$(mysql -h $HT_RIGHTS_DB_HOST -u htrc ht -N --execute \
  "select time from rights_current where time > '$LAST_UPDATE_TIME' order by \
  time desc limit 1;")

if [ -z "$NEW_TIME_OF_LAST_UPDATE" ]
then
  # if there are no new values since the last update, set the time of the last
  # update to the current time at $HT_RIGHTS_DB_HOST
  mysql -h $HT_RIGHTS_DB_HOST -u htrc ht -N --execute \
  "select now();" > $RIGHTS_UPDATE_TIMESTAMP_FILE
else
  echo $NEW_TIME_OF_LAST_UPDATE > $RIGHTS_UPDATE_TIMESTAMP_FILE
fi