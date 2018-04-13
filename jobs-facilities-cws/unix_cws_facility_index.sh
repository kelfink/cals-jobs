#!/usr/bin/env bash

sudo gradle shadowJar

rm -rf cws-out
mkdir cws-out

java -Dlog4j.configuration=file:log4j.properties -jar build/libs/cws-facilities-job-0.6.3-SNAPSHOT.jar \
     -c config/cws-facility-job_local_unix.yaml -l ./cws-out/ \
     > ./cws-out/out_Facility.txt 2>&1
