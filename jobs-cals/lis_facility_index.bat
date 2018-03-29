@echo off
call gradle clean shadowJar
call mkdir lis-out
java -Dlog4j.configuration=file:log4j.properties -cp build/libs/cals-jobs-0.6.2-SNAPSHOT.jar gov.ca.cwds.jobs.cals.facility.lis.LisFacilityJobRunner ^
     -c config/cals/facility/lis-facility-job.yaml -l ./lis-out/ ^
     > ./lis-out/out_Facility.txt 2>&1
