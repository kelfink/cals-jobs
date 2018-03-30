@echo off
call gradle clean shadowJar
call mkdir cws-out
java -Dlog4j.configuration=file:log4j.properties -cp build/libs/cals-jobs-0.6.2-SNAPSHOT.jar gov.ca.cwds.jobs.cals.facility.cws.CwsFacilityJobRunner ^
     -c config/cals/facility/cws-facility-job.yaml -l ./cws-out/ ^
     > ./cws-out/out_Facility.txt 2>&1
