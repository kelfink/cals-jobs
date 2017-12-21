package gov.ca.cwds.jobs.cals.rfa;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.CalsnsSessionFactory;
import gov.ca.cwds.cals.service.dto.changed.ChangedRFA1aFormDTO;
import gov.ca.cwds.cals.service.rfa.RFA1aFormsCollectionService;
import gov.ca.cwds.generic.jobs.util.JobReader;
import org.hibernate.SessionFactory;

import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * @author CWDS TPT-2
 */
public class RFA1aFormReader implements JobReader<ChangedRFA1aFormDTO> {

  private Iterator<ChangedRFA1aFormDTO> changedRFA1aFormDTOIterator;

  @Inject
  private RFA1aFormIncrementalLoadDateStrategy incrementalLoadDateStrategy;

  @Inject
  @CalsnsSessionFactory
  private SessionFactory calsnsSessionFactory;

  @Inject
  private RFA1aFormsCollectionService rfa1aFormsCollectionService;

  @Override
  public void init() {
    LocalDateTime dateAfter = incrementalLoadDateStrategy.calculateLocalDateTime();
    calsnsSessionFactory.getCurrentSession().beginTransaction();
    changedRFA1aFormDTOIterator = rfa1aFormsCollectionService.streamChangedRFA1aForms(dateAfter)
        .iterator();
  }

  @Override
  public ChangedRFA1aFormDTO read() {
    return changedRFA1aFormDTOIterator.hasNext() ? changedRFA1aFormDTOIterator.next() : null;
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
