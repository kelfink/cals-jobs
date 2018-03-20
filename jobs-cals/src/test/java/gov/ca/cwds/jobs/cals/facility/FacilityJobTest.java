package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.AbstractModule;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.jobs.cals.facility.inject.FacilityJobModule;
import gov.ca.cwds.jobs.common.job.BulkWriter;
import gov.ca.cwds.jobs.common.job.impl.JobRunner;
import gov.ca.cwds.jobs.common.job.timestamp.LastRunDirHelper;
import gov.ca.cwds.jobs.common.job.utils.ConsumerCounter;
import gov.ca.cwds.test.support.DatabaseHelper;
import io.dropwizard.db.DataSourceFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Alexander Serbin on 3/18/2018.
 */

@Ignore
public class FacilityJobTest {


    private LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("temp");

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.out.println("Setup database has been started!!!");
        FacilityJobConfiguration configuration = getFacilityJobConfiguration();
        DatabaseHelper.setUpDatabase(configuration.getLisDataSourceFactory(), DataSourceName.LIS);
        DatabaseHelper.setUpDatabase(configuration.getFasDataSourceFactory(), DataSourceName.FAS);
        DatabaseHelper.setUpDatabase(configuration.getCmsDataSourceFactory(), DataSourceName.CWSRS);
        System.out.println("Setup database has been finished!!!");
    }

    private static FacilityJobConfiguration getFacilityJobConfiguration() {
        FacilityJobConfiguration facilityJobConfiguration =
                FacilityJobConfiguration.getJobsConfiguration(FacilityJobConfiguration.class, getConfigFilePath());
        fixDatasourceFactory(facilityJobConfiguration.getCmsDataSourceFactory());
        fixDatasourceFactory(facilityJobConfiguration.getLisDataSourceFactory());
        fixDatasourceFactory(facilityJobConfiguration.getCalsnsDataSourceFactory());
        fixDatasourceFactory(facilityJobConfiguration.getFasDataSourceFactory());
        return facilityJobConfiguration;
    }

    private static void fixDatasourceFactory(DataSourceFactory dataSourceFactory) {
        dataSourceFactory.setUrl(dataSourceFactory.getProperties().get("hibernate.connection.url"));
        dataSourceFactory.setUser(dataSourceFactory.getProperties().get("hibernate.connection.username"));
        dataSourceFactory.setPassword(dataSourceFactory.getProperties().get("hibernate.connection.password"));
    }

    @Test
    public void facilityJobTest() throws IOException {
        assertEquals(0, ConsumerCounter.getCounter());
        FacilityJobModule facilityJobModule = new FacilityJobModule(getModuleArgs());
        facilityJobModule.setElasticSearchModule(new AbstractModule() {
            @Override
            protected void configure() {

            }
        });

        facilityJobModule.setFacilityElasticWriterClass(StubWriter.class);
        JobRunner.run(facilityJobModule);
        assertEquals(309, ConsumerCounter.getCounter());
    }

    private String[] getModuleArgs() {
        return new String[]{"-c", getConfigFilePath(), "-l", lastRunDirHelper.getLastRunDir().toString()};
    }

    private static String getConfigFilePath() {
        return Paths.get("src", "test", "resources", "cals", "facility", "test-facility-job.yaml").normalize().toAbsolutePath().toString();
    }

    static class StubWriter implements BulkWriter<ChangedFacilityDTO> {
        @Override
        public void write(List<ChangedFacilityDTO> items) {
            ConsumerCounter.addToCounter(items.size());
        }
    }

}