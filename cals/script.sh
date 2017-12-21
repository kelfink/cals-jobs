#!/usr/bin/env bash

sudo gradle shadowJar

mkdir out

java -DDB_FAS_JDBC_URL="jdbc:postgresql://localhost:5432/?currentSchema=fas" \
        -DDB_FAS_USER="postgres_data" -DDB_FAS_PASSWORD="postgres_data" \
     -DDB_LIS_JDBC_URL="jdbc:postgresql://localhost:5432/?currentSchema=lis" \
        -DDB_LIS_USER="postgres_data" -DDB_LIS_PASSWORD="postgres_data" \
     -DDB_CMS_JDBC_URL="jdbc:db2://localhost:50000/DB0TDEV" -DDB_CMS_SCHEMA="CWSCMSRS" \
        -DDB_CMS_USER="db2inst1" -DDB_CMS_PASSWORD="db2inst1-pwd" \
     -DDB_CALSNS_JDBC_URL="jdbc:postgresql://localhost:5432/?currentSchema=calsns" \
        -DDB_CALSNS_USER="postgres_data" -DDB_CALSNS_PASSWORD="postgres_data" \
     -cp build/libs/cals-jobs-0.37.jar gov.ca.cwds.jobs.cals.facility.FacilityIndexerJob \
      -c config/cals/facility/facility.yaml -l ./out/ \
      > ./out/out_Facility.txt 2>&1