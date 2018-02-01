package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import gov.ca.cwds.cals.CompositeIterator;
import gov.ca.cwds.cals.service.FacilityService;
import gov.ca.cwds.cals.service.builder.FacilityParameterObjectCMSAwareBuilder;
import gov.ca.cwds.cals.service.dto.FacilityDTO;
import gov.ca.cwds.cals.util.DateTimeUtils;
import gov.ca.cwds.jobs.cals.RecordChangeOperation;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;
import static gov.ca.cwds.cals.Constants.UnitOfWork.FAS;
import static gov.ca.cwds.cals.Constants.UnitOfWork.LIS;

/**
 * @author CWDS TPT-2
 */
public class ChangedFacilityService extends FacilityService {

  private static final Logger LOG = LoggerFactory.getLogger(ChangedFacilityService.class);

  @Inject
  private RecordChangeCwsCmsDao recordChangeCwsCmsDao;

  @Inject
  private RecordChangeLisDao recordChangeLisDao;

  @Inject
  private RecordChangeFasDao recordChangeFasDao;

  @Inject
  private FacilityParameterObjectCMSAwareBuilder facilityParameterObjectBuilder;

  public ChangedFacilityService() {
    // default constructor
  }

  @UnitOfWork(CMS)
  protected RecordChanges handleCwsCmsFacilityIds(Date dateAfter) {
    RecordChanges recordChanges = new RecordChanges();
    recordChangeCwsCmsDao.streamChangedFacilityRecords(buildDateForCwsCms(dateAfter)).forEach(recordChanges::add);
    return recordChanges;
  }

  private Date buildDateForCwsCms(Date date) {
    return date == null ? DateTimeUtils.toDate(LocalDateTime.now().minusYears(100)) : date;
  }

  @UnitOfWork(LIS)
  protected RecordChanges handleLisFacilityIds(Date lisAfter) {
    RecordChanges recordChanges = new RecordChanges();
    recordChangeLisDao.streamChangedFacilityRecords(lisAfter).forEach(recordChanges::add);
    return recordChanges;
  }

  @UnitOfWork(FAS)
  protected RecordChanges handleFasFacilityIds(Date after) {
    RecordChanges recordChanges = new RecordChanges();
    recordChangeFasDao.streamChangedFacilityRecords(after == null, evalDateAfter(after))
        .forEach(recordChanges::add);
    return recordChanges;
  }

  protected FacilityDTO findExpandedById(String id) {
    return findByParameterObject(facilityParameterObjectBuilder.createExpandedFacilityParameterObject(id));
  }

  public Stream<ChangedFacilityDTO> changedFacilitiesStream(Date after, Date lisAfter) {
    RecordChanges cwsCmsRecordChanges = handleCwsCmsFacilityIds(after);
    RecordChanges fasRecordChanges = handleFasFacilityIds(after);
    RecordChanges lisRecordChanges = handleLisFacilityIds(lisAfter);
    Stream<RecordChange> stream =
        Stream.concat(cwsCmsRecordChanges.newStream(), fasRecordChanges.newStream());

    return Stream.concat(stream, lisRecordChanges.newStream())
        .map(recordChange -> {
          LOG.info("Getting facility by ID: {}", recordChange.getId());
          FacilityDTO facilityDTO = findExpandedById(recordChange.getId());
          if (facilityDTO == null) {
            return null;
          }
          LOG.info("Find facility by ID {} returned FacilityDTO with ID {}", recordChange.getId(),
              facilityDTO.getId());
          return new ChangedFacilityDTO(facilityDTO, recordChange.getRecordChangeOperation());
        })
        .filter(facilityDTO -> {
          if (facilityDTO == null) {
            LOG.error("Finding facility by ID did not return FacilityDTO. Skipped.");
            return false;
          }
          return true;
        })
        .filter(facilityDTO -> {
          if (facilityDTO.getId() == null) {
            LOG.error(
                "Finding facility by ID returned incorrect FacilityDTO with NULL id. Skipped.");
            return false;
          } else {
            return true;
          }
        });
  }

  private Date evalDateAfter(Date after) {
    return after != null ? after
        : Date.from(LocalDateTime.now().minusYears(100).atZone(ZoneId.systemDefault()).toInstant());
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
