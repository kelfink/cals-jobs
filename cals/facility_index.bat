@echo off
call gradle shadowJar
call mkdir out
java -DDB_FAS_JDBC_URL="jdbc:postgresql://192.168.99.100:32770/?currentSchema=fas" ^
       -DDB_FAS_USER="postgres_data" -DDB_FAS_PASSWORD="postgres_data" ^
    -DDB_LIS_JDBC_URL="jdbc:postgresql://192.168.99.100:32770/?currentSchema=lis" ^
       -DDB_LIS_USER="postgres_data" -DDB_LIS_PASSWORD="postgres_data" ^
    -DDB_CMS_JDBC_URL="jdbc:db2://192.168.99.100:32768/DB0TDEV" -DDB_CMS_SCHEMA="CWSCMSRS" ^
       -DDB_CMS_USER="db2inst1" -DDB_CMS_PASSWORD="db2inst1-pwd" ^
    -DDB_CALSNS_JDBC_URL="jdbc:postgresql://192.168.99.100:32770/?currentSchema=calsns" ^
       -DDB_CALSNS_USER="postgres_data" -DDB_CALSNS_PASSWORD="postgres_data" ^
    -cp build/libs/cals-jobs-0.5.4-SNAPSHOT.jar gov.ca.cwds.jobs.cals.facility.FacilityIndexerJob ^
     -c config/cals/facility/facility.yaml -l ./out/ ^
     > ./out/out_Facility.txt 2>&1
