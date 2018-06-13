#!/usr/bin/env bash

sudo gradle shadowJar

rm -rf cws-out
mkdir cws-out

java -Dlog4j.configuration=file:log4j.properties -jar build/libs/jobs-cap-users-1.0.0-SNAPSHOT.jar \
     -c config/unix_cap-users-job.yaml -l ./cws-out/ \
     > ./cws-out/out_CAP_USERS.txt 2>&1
