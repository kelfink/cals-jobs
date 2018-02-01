package gov.ca.cwds.jobs.cals.rfa;

import com.google.inject.Inject;
import gov.ca.cwds.cals.persistence.dao.calsns.RFA1aFormsDao;
import gov.ca.cwds.cals.service.mapper.RFA1aFormMapper;
import gov.ca.cwds.jobs.cals.RecordChangeOperation;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 1/30/2018.
 */
public class ChangedRFAFormsService {

    @Inject
    private RFA1aFormsDao dao;

    @Inject
    private RFA1aFormMapper rfa1aFormMapper;

    public Stream<ChangedRFA1aFormDTO> streamChangedRFA1aForms(LocalDateTime after) {
        return dao.streamChangedRFA1aForms(after).map(
                rfa1aForm -> new ChangedRFA1aFormDTO(rfa1aFormMapper.toExpandedRFA1aFormDTO(rfa1aForm),
                        RecordChangeOperation.I));
    }

}
