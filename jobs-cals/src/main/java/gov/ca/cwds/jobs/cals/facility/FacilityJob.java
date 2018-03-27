package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.common.job.impl.JobImpl;
import org.hibernate.SessionFactory;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class FacilityJob extends JobImpl<ChangedFacilityDTO> {

  @Inject
  @FasSessionFactory
  private SessionFactory fasSessionFactory;

  @Inject
  @CmsSessionFactory
  private SessionFactory cmsSessionFactory;

  @Inject
  @LisSessionFactory
  private SessionFactory lisSessionFactory;

  @Override
  public void close() {
    super.close();
    fasSessionFactory.close();
    lisSessionFactory.close();
    cmsSessionFactory.close();
  }

}
