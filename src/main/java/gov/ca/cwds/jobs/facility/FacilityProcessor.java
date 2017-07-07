package gov.ca.cwds.jobs.facility;

import java.io.Serializable;

import gov.ca.cwds.data.model.facility.es.ESFacility;
import gov.ca.cwds.data.model.facility.es.ESFacilityAddress;
import gov.ca.cwds.jobs.util.JobProcessor;

/**
 * @author CWDS Elasticsearch Team
 */
public class FacilityProcessor implements JobProcessor<FacilityRow, ESFacility>, Serializable {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  @Override
  public ESFacility process(FacilityRow item) {
    ESFacility ret = new ESFacility();

    ret.setId(item.getId());
    ret.setType(item.getType());
    ret.setName(item.getName());
    ret.setLicenseeName(item.getLicenseeName());
    ret.setAssignedWorker(item.getAssignedWorker());
    ret.setDistrictOffice(item.getDistrictOffice());
    ret.setLicenseNumber(item.getLicenseNumber());
    ret.setLicenseStatus(item.getLicenseStatus());
    ret.setCapacity(item.getCapacity());
    ret.setLicenseEffectiveDate(item.getLicenseEffectiveDate());
    ret.setOriginalApplicationReceivedDate(item.getOriginalApplicationReceivedDate());
    ret.setLastVisitDate(item.getLastVisitDate());
    ret.setLastVisitReason(item.getLastVisitReason());
    ret.setCounty(item.getCounty());
    ret.setPrimaryPhoneNumber(item.getPrimaryPhoneNumber());
    ret.setAltPhoneNumber(item.getAltPhoneNumber());

    ESFacilityAddress adr = new ESFacilityAddress();
    adr.setStateCodeType(item.getStateCodeType());
    adr.setZipCode(item.getZipCode());
    adr.setZipSuffixCode(item.getZipSuffixCode());
    adr.setStreetAddress(item.getStreetAddress());
    adr.setCity(item.getCity());
    adr.setCounty(item.getCounty());
    ret.setAddress(adr);

    return ret;
  }

}
