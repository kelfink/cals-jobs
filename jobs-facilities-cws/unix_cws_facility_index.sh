#!/usr/bin/env bash

sudo gradle shadowJar

rm -rf cws-out
mkdir cws-out

java -Dlog4j.configuration=file:log4j.properties -cp build/libs/jobs-facilities-cws-0.6.2-SNAPSHOT.jar gov.ca.cwds.jobs.cals.facility.FacilityJobRunner \
     -c config/facility-job.yaml -l ./out/ \
     > ./cws-out/out_Facility.txt 2>&1