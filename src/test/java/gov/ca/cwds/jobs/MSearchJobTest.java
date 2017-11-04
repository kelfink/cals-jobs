package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;

public class MSearchJobTest
    extends Goddard<ReplicatedOtherAdultInPlacemtHome, ReplicatedOtherAdultInPlacemtHome> {

  ReplicatedOtherAdultInPlacemtHomeDao dao;
  MSearchJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedOtherAdultInPlacemtHomeDao(this.sessionFactory);
    target = new MSearchJob(dao, esDao, MAPPER, sessionFactory, null, flightRecorder, opts);
  }

  @Test
  public void type() throws Exception {
    assertThat(MSearchJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void executeJob_Args__Date() throws Exception {
    Date lastSuccessfulRunTime = new Date();
    Date actual = target.executeJob(lastSuccessfulRunTime);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void main_Args__StringArray() throws Exception {
    final String[] args = new String[] {"-c", "config/local.yaml", "-l",
        "/Users/CWS-NS3/client_indexer_time.txt", "-S"};
    MSearchJob.main(args);
  }

}
