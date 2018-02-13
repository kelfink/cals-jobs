package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.CalsnsSessionFactory;
import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.common.job.impl.AbstractJobReader;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class FacilityReader extends AbstractJobReader<ChangedFacilityDTO> {

  @Inject
  public FacilityReader(ChangedFacilityService changedEntitiesService,
                        TimestampOperator timestampOperator) {
    super(changedEntitiesService, timestampOperator);
  }

  @Inject
  @FasSessionFactory
  private SessionFactory fasSessionFactory;

  @Inject
  @LisSessionFactory
  private SessionFactory lisSessionFactory;

  @Inject
  @CmsSessionFactory
  private SessionFactory cwsCmcSessionFactory;

  @Inject
  @CalsnsSessionFactory
  private SessionFactory calsnsSessionFactory;

  @Override
  public void init() {
    fasSessionFactory.getCurrentSession().beginTransaction();
    lisSessionFactory.getCurrentSession().beginTransaction();
    cwsCmcSessionFactory.getCurrentSession().beginTransaction();
    super.init();
  }

  @Override
  public void destroy() {
    try {
      closeSessionFactory(fasSessionFactory);
    } finally {
      try {
        closeSessionFactory(lisSessionFactory);
      } finally {
        try {
          closeSessionFactory(cwsCmcSessionFactory);
        } finally {
          closeSessionFactory(calsnsSessionFactory);
        }
      }
    }
  }

  private void closeSessionFactory(SessionFactory sessionFactory) {
    try {
      sessionFactory.getCurrentSession().getTransaction().rollback();
    } finally {
      sessionFactory.close();
    }
  }

}
