package gov.ca.cwds.jobs.cals.facility.cws.identifier;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.cws.dao.CwsChangedIdentifierDao;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */

public class CwsChangedEntitiesIdentifiersService implements
    ChangedEntitiesIdentifiersService<TimestampSavePoint<LocalDateTime>> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(CwsChangedEntitiesIdentifiersService.class);

  @Inject
  private CwsChangedIdentifierDao recordChangeCwsCmsDao;

  @Override
  @UnitOfWork(CMS)
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiersForInitialLoad(
      PageRequest pageRequest) {
    List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> identifiers = recordChangeCwsCmsDao
        .getInitialLoadStream(pageRequest);
    removeEmptyIdentifiers(identifiers);
    return identifiers;
  }

  @Override
  @UnitOfWork(CMS)
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiersForResumingInitialLoad(
      TimestampSavePoint<LocalDateTime> timeStampAfter, PageRequest pageRequest) {
    List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> identifiers =
        recordChangeCwsCmsDao
            .getResumeInitialLoadStream(timeStampAfter.getTimestamp(), pageRequest);
    removeEmptyIdentifiers(identifiers);
    return identifiers;
  }

  @Override
  @UnitOfWork(CMS)
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiersForIncrementalLoad(
      TimestampSavePoint<LocalDateTime> savePoint,
      PageRequest pageRequest) {
    List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> identifiers = recordChangeCwsCmsDao
        .getIncrementalLoadStream(savePoint.getTimestamp(), pageRequest);
    removeEmptyIdentifiers(identifiers);
    return identifiers;
  }

  private void removeEmptyIdentifiers(
      List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> identifiers) {
    boolean emptyIdsFound = identifiers.removeIf(o -> StringUtils.isEmpty(o.getId()));
    LOGGER.info("Empty identifiers removed {}", emptyIdsFound);
  }

}
