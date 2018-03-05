package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import com.jcabi.aspects.Loggable;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.CompositeIterator;
import gov.ca.cwds.cals.service.FacilityService;
import gov.ca.cwds.cals.service.builder.FacilityParameterObjectBuilder;
import gov.ca.cwds.cals.service.dto.FacilityDTO;
import gov.ca.cwds.cals.web.rest.parameter.FacilityParameterObject;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.job.ChangedEntitiesService;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;
import static gov.ca.cwds.cals.Constants.UnitOfWork.LIS;

/**
 * @author CWDS TPT-2
 */
public class ChangedFacilityService extends FacilityService implements ChangedEntitiesService<ChangedFacilityDTO> {

  private static final Logger LOG = LoggerFactory.getLogger(ChangedFacilityService.class);

  static final DateTimeFormatter lisTimestampFormatter = DateTimeFormatter.ofPattern("YYYYMMddHHmmss");

  @Inject
  private RecordChangeCwsCmsDao recordChangeCwsCmsDao;

  @Inject
  private RecordChangeLisDao recordChangeLisDao;

  @Inject
  private FacilityParameterObjectBuilder facilityParameterObjectBuilder;

  public ChangedFacilityService() {
    // default constructor
  }

  @Override
  public Stream<ChangedFacilityDTO> doInitialLoad() {
    if (LOG.isInfoEnabled()) {
      LOG.info("Processing initial load");
    }
    return handleFacilitiesIdentifiersStream(getCwsCmsInitialLoadIdentifiers(), getLisInitialLoadIdentifiers());
  }

  @Override
  public Stream<ChangedFacilityDTO> doIncrementalLoad(LocalDateTime dateAfter) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Processing incremental load after timestamp " + TimestampOperator.DATE_TIME_FORMATTER.format(dateAfter));
    }
    return handleFacilitiesIdentifiersStream(getCwsCmsIncrementalLoadIdentifiers(dateAfter),
            getLisIncrementalLoadIdentifiers(dateAfter));
  }

  @Loggable(Loggable.DEBUG)
  @UnitOfWork(CMS)
  protected RecordChanges getCwsCmsInitialLoadIdentifiers() {
    RecordChanges recordChanges = new RecordChanges();
    recordChangeCwsCmsDao.getInitialLoadStream().forEach(recordChanges::add);
    return recordChanges;
  }

  @Loggable(Loggable.DEBUG)
  @UnitOfWork(CMS)
  protected RecordChanges getCwsCmsIncrementalLoadIdentifiers(LocalDateTime dateAfter) {
    RecordChanges recordChanges = new RecordChanges();
    recordChangeCwsCmsDao.getIncrementalLoadStream(dateAfter).forEach(recordChanges::add);
    return recordChanges;
  }

  @Loggable(Loggable.DEBUG)
  @UnitOfWork(LIS)
  protected RecordChanges getLisInitialLoadIdentifiers() {
    RecordChanges recordChanges = new RecordChanges();
    recordChangeLisDao.getInitialLoadStream().forEach(recordChanges::add);
    return recordChanges;
  }

  @Loggable(Loggable.DEBUG)
  @UnitOfWork(LIS)
  protected RecordChanges getLisIncrementalLoadIdentifiers(LocalDateTime timestampAfter) {
    RecordChanges recordChanges = new RecordChanges();
    BigInteger dateAfter = new BigInteger(lisTimestampFormatter.format(timestampAfter));
    recordChangeLisDao.getIncrementalLoadStream(dateAfter).forEach(recordChanges::add);
    return recordChanges;
  }

  @Loggable(Loggable.DEBUG)
  @UnitOfWork(CMS)
  protected FacilityParameterObject createFacilityParameterObject(String id) {
    return facilityParameterObjectBuilder.createFacilityParameterObject(id);
  }

  @Loggable(Loggable.DEBUG)
  protected FacilityDTO findFacilityById(String id) {
    try {
      FacilityDTO facilityDTO = findByParameterObject(createFacilityParameterObject(id));
      if (facilityDTO == null) {
        LOG.error("Can't get facility by id " + id);
        throw new IllegalStateException("FacilityDTO must not be null!!!");
      } else {
        if (LOG.isInfoEnabled()) {
          LOG.info("Found facility by ID {}", facilityDTO.getId());
        }
      }
      return facilityDTO;
    } catch (Exception e) {
      LOG.error("Can't get facility by id " + id, e);
      throw new IllegalStateException(String.format("Can't get facility by id %s", id), e);
    }
  }

  @Loggable(Loggable.DEBUG)
  private Stream<ChangedFacilityDTO> handleFacilitiesIdentifiersStream(RecordChanges cwscmsIdentifiers, RecordChanges lisIdentifiers) {
    if (LOG.isInfoEnabled()) {
      printRecordsCount(cwscmsIdentifiers, DataSourceName.CWS);
      printRecordsCount(lisIdentifiers, DataSourceName.LIS);
    }

    Stream<RecordChange> stream = cwscmsIdentifiers.newStream();

    return Stream.concat(stream, lisIdentifiers.newStream())
            .map(recordChange -> new ChangedFacilityDTO(findFacilityById(recordChange.getId()),
                    recordChange.getRecordChangeOperation()))
            .filter(Objects::nonNull);
  }


  private void printRecordsCount(RecordChanges recordChanges, DataSourceName dataSourceName) {
    String messageFormatString = "Found {} facilities from {} {} elastic search facility index";
    LOG.info(messageFormatString, recordChanges.toBeInserted.size(), dataSourceName.name(), "to be inserted to");
    LOG.info(messageFormatString, recordChanges.toBeUpdated.size(), dataSourceName.name(), "to be updated in");
    LOG.info(messageFormatString, recordChanges.toBeDeleted.size(), dataSourceName.name(), "to be deleted from");
  }

  private static class RecordChanges {

    private HashMap<String, RecordChange> toBeDeleted = new HashMap<>();
    private HashMap<String, RecordChange> toBeInserted = new HashMap<>();
    private HashMap<String, RecordChange> toBeUpdated = new HashMap<>();

    void add(RecordChange data) {
      if (RecordChangeOperation.D == data.getRecordChangeOperation()) {
        toBeDeleted.put(data.getId(), data);
      } else if (RecordChangeOperation.I == data.getRecordChangeOperation()) {
        toBeInserted.put(data.getId(), data);
      } else if (RecordChangeOperation.U == data.getRecordChangeOperation()) {
        toBeUpdated.put(data.getId(), data);
      }
    }

    private Iterable<RecordChange> newIterable() {
      compact();
      return () -> new CompositeIterator<>(
          toBeDeleted.values().iterator(),
          toBeInserted.values().iterator(),
          toBeUpdated.values().iterator()
      );
    }

    Stream<RecordChange> newStream() {
      return StreamSupport.stream(this.newIterable().spliterator(), false);
    }

    private void compact() {
      toBeDeleted.forEach((id, e) -> {
        toBeInserted.remove(id);
        toBeUpdated.remove(id);
      });
      toBeInserted.forEach((id, e) -> toBeUpdated.remove(id));
    }
  }
}
