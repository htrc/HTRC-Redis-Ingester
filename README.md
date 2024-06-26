# HTRC-Redis-Ingester

A tool to read volume access level data from a file and ingest it into a Redis database.

## Build

Create an executable jar containing all dependencies, i.e., a fat jar, using
the following command:
```
mvn clean install
```

## Run
```
java -jar HTRC-Redis-Ingester-0.0.1-SNAPSHOT-jar-with-dependencies.jar -l "$ACCESS_LEVEL_TSV_FILE" -h "$REDIS_HOST" -p "$REDIS_PASSWORD"
```
ACCESS_LEVEL_TSV_FILE: The path to the TSV file containing volume access level data. This file is created by accessing HT Rights DB(```host- bourgogne.umdl.umich.edu, db name- rights_current```) and extracting volume access level data.
Example of the TSV file:
```
aeu.ark:/13960/t5gb3bf7f        1A
coo.31924024787057      1A
coo1.ark:/13960/s24q4zrgx97     1A
coo1.ark:/13960/t1ck5xp0c       1A
coo1.ark:/13960/t47q6hp8m       1A
coo1.ark:/13960/t73v6r52v       1A
coo1.ark:/13960/t8sc0jt1s       1A
coo1.ark:/13960/t9h428w01       1A
coo1.ark:/13960/t9s25hc80       1A
coo1.ark:/13960/t9t22jf2k       1A
dul1.ark:/13960/s20x2zk53nt     1A
dul1.ark:/13960/s21tnhjvfm0     1A
dul1.ark:/13960/s22jnknjcpf     1A
dul1.ark:/13960/s24x6nnsrjm     1A
dul1.ark:/13960/s28chh028zt     1A
dul1.ark:/13960/s2djwr1k5fq     1A
```

## Configuration

HTRC-Redis-Ingester loads configuration settings from a file,
src/main/resources/config.properties. An example of this file is shown below:

```
redis-host=localhost

redis-num-hmsets-per-pipeline=1000

# redis contains keys of the form volume:<volid>:info mapped to hashes with
# 2 fields: access-level and avail-status
redis-access-level-hash-field-name=access-level
redis-avail-status-hash-field-name=avail-status

redis-volume-id-key-prefix=volume:
redis-volume-id-key-suffix=:info
```

## Monitoring

Logs are recorded in the file, ```htrc-redis-ingester.log``` and the file path is configured in ``src/main/resources/logback.xml``.
