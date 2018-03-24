package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.jobs.cals.facility.recordchange.CwsRecordChange;
import gov.ca.cwds.jobs.cals.facility.recordchange.LisRecordChange;
import gov.ca.cwds.jobs.cals.facility.recordchange.RecordChangeCwsCmsDao;
import gov.ca.cwds.jobs.cals.facility.recordchange.RecordChangeLisDao;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.ChangedIdentifiersService;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;
import static gov.ca.cwds.cals.Constants.UnitOfWork.LIS;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class ChangedFacilityIdentifiersServiceImpl implements ChangedIdentifiersService {

    private static final Logger LOG = LoggerFactory.getLogger(ChangedFacilityIdentifiersServiceImpl.class);

    @Inject
    private RecordChangeCwsCmsDao recordChangeCwsCmsDao;

    @Inject
    private RecordChangeLisDao recordChangeLisDao;

    @Override
    public Stream<ChangedEntityIdentifier> getIdentifiersForInitialLoad() {
        return concat(getCwsCmsInitialLoadIdentifiers(),
                getLisInitialLoadIdentifiers());
    }

    @Override
    public Stream<ChangedEntityIdentifier> getIdentifiersForResumingInitialLoad(LocalDateTime timeStampAfter) {
        return concat(getCwsCmsResumingInitialLoadIdentifiers(timeStampAfter),
                getLisIncrementalLoadIdentifiers(timeStampAfter));
    }

    @UnitOfWork(CMS)
    protected ChangedFacilitiesIdentifiers getCwsCmsResumingInitialLoadIdentifiers(LocalDateTime timeStampAfter) {
        ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers(DataSourceName.CWS);
        recordChangeCwsCmsDao.getResumeInitialLoadStream(timeStampAfter).
                map(CwsRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
        return changedEntityIdentifiers;
    }

    @Override
    public Stream<ChangedEntityIdentifier> getIdentifiersForIncrementalLoad(LocalDateTime timestamp) {
        return concat(getCwsCmsIncrementalLoadIdentifiers(timestamp),
                getLisIncrementalLoadIdentifiers(timestamp));
    }

    @UnitOfWork(CMS)
    protected ChangedFacilitiesIdentifiers getCwsCmsInitialLoadIdentifiers() {
        ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers(DataSourceName.CWS);
        recordChangeCwsCmsDao.getInitialLoadStream().
                map(CwsRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
        return changedEntityIdentifiers;
    }

    @UnitOfWork(CMS)
    protected ChangedFacilitiesIdentifiers getCwsCmsIncrementalLoadIdentifiers(LocalDateTime dateAfter) {
        ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers(DataSourceName.CWS);
        recordChangeCwsCmsDao.getIncrementalLoadStream(dateAfter).
                map(CwsRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
        return changedEntityIdentifiers;
    }

    @UnitOfWork(LIS)
    protected ChangedFacilitiesIdentifiers getLisInitialLoadIdentifiers() {
        ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers(DataSourceName.LIS);
        recordChangeLisDao.getInitialLoadStream().
                map(LisRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
        return changedEntityIdentifiers;
    }

    @UnitOfWork(LIS)
    protected ChangedFacilitiesIdentifiers getLisIncrementalLoadIdentifiers(LocalDateTime timestampAfter) {
        ChangedFacilitiesIdentifiers changedEntityIdentifiers = new ChangedFacilitiesIdentifiers(DataSourceName.LIS);
        BigInteger dateAfter = new BigInteger(LisRecordChange.lisTimestampFormatter.format(timestampAfter));
        recordChangeLisDao.getIncrementalLoadStream(dateAfter).
                map(LisRecordChange::valueOf).forEach(changedEntityIdentifiers::add);
        return changedEntityIdentifiers;
    }

    private Stream<ChangedEntityIdentifier> concat(ChangedFacilitiesIdentifiers cwscmsIdentifiers,
                                                 ChangedFacilitiesIdentifiers lisIdentifiers) {
        return Stream.concat(cwscmsIdentifiers.newStream(), lisIdentifiers.newStream()).filter(Objects::nonNull);
    }

}
