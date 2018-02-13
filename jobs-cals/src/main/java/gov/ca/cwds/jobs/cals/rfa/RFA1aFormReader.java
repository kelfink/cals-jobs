package gov.ca.cwds.jobs.cals.rfa;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.CalsnsSessionFactory;
import gov.ca.cwds.jobs.common.job.impl.AbstractJobReader;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class RFA1aFormReader extends AbstractJobReader<ChangedRFA1aFormDTO> {

  @Inject
  public RFA1aFormReader(ChangedRFAFormsService changedRFA1aFormsService,
                         TimestampOperator timestampOperator) {
    super(changedRFA1aFormsService, timestampOperator);
  }

  @Inject
  @CalsnsSessionFactory
  private SessionFactory calsnsSessionFactory;

  @Override
  public void init() {
    calsnsSessionFactory.getCurrentSession().beginTransaction();
    super.init();
  }

  @Override
  public void destroy() {
    closeSessionFactory(calsnsSessionFactory);
  }

  private void closeSessionFactory(SessionFactory sessionFactory) {
    try {
      sessionFactory.getCurrentSession().getTransaction().rollback();
    } finally {
      sessionFactory.close();
    }
  }

}
