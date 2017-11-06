package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AttorneyIndexerJobTest {

  @Test
  public void type() throws Exception {
    assertThat(AttorneyIndexerJob.class, notNullValue());
  }

  @Test
  public void main_Args__StringArray() throws Exception {
    String[] args = new String[] {"-c", "config/local.yaml", "-l",
        "/var/lib/jenkins/client_indexer_time.txt", "-S"};
    AttorneyIndexerJob.main(args);
  }

}
