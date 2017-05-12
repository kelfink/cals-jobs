package gov.ca.cwds.jobs.facility;

import gov.ca.cwds.data.model.facility.es.ESFacility;
import gov.ca.cwds.data.model.facility.es.ESFacilityAddress;
import gov.ca.cwds.jobs.util.JobProcessor;

/**
 * @author CWDS Elasticsearch Team
 */
public class FacilityProcessor implements JobProcessor<FacilityRow, ESFacility>{
  @Override
  public ESFacility process(FacilityRow item) throws Exception {
    ESFacility esFacility = new ESFacility();
    esFacility.setId(item.getId());
    esFacility.setType(item.getType());
    esFacility.setName(item.getName());
    esFacility.setLicenseeName(item.getLicenseeName());
    esFacility.setAssignedWorker(item.getAssignedWorker());
    esFacility.setDistrictOffice(item.getDistrictOffice());
    esFacility.setLicenseNumber(item.getLicenseNumber());
    esFacility.setLicenseStatus(item.getLicenseStatus());
    esFacility.setCapacity(item.getCapacity());
    esFacility.setLicenseEffectiveDate(item.getLicenseEffectiveDate());
    esFacility.setOriginalApplicationReceivedDate(item.getOriginalApplicationReceivedDate());
    esFacility.setLastVisitDate(item.getLastVisitDate());
    esFacility.setLastVisitReason(item.getLastVisitReason());
    esFacility.setCounty(item.getCounty());
    esFacility.setPrimaryPhoneNumber(item.getPrimaryPhoneNumber());
    esFacility.setAltPhoneNumber(item.getAltPhoneNumber());

    ESFacilityAddress esFacilityAddress = new ESFacilityAddress();
    esFacilityAddress.setStateCodeType(item.getStateCodeType());
    esFacilityAddress.setZipCode(item.getZipCode());
    esFacilityAddress.setZipSuffixCode(item.getZipSuffixCode());
    esFacilityAddress.setStreetAddress(item.getStreetAddress());
    esFacilityAddress.setCity(item.getCity());
    esFacilityAddress.setCounty(item.getCounty());
    esFacility.setAddress(esFacilityAddress);

    return esFacility;
  }
}
