@echo off
call gradle shadowJar
call mkdir out
java -cp build/libs/cals-jobs-0.6.2-SNAPSHOT.jar gov.ca.cwds.jobs.cals.rfa.RFA1aFormJobRunner ^
     -c config/cals/rfa/CALS_RFA1aForm.yaml -l ./out/ ^
     > ./out/out_rfa.txt 2>&1
