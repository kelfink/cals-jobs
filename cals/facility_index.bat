@echo off
call gradle shadowJar
call mkdir out
java -cp build/libs/cals-jobs-0.5.5-SNAPSHOT.jar gov.ca.cwds.jobs.cals.facility.FacilityIndexerJob ^
     -c config/cals/facility/facility-job.yaml -l ./out/ ^
     > ./out/out_Facility.txt 2>&1
