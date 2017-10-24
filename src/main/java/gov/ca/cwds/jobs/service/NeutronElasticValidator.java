package gov.ca.cwds.jobs.service;

import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedClientAddressDao;
import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.dao.cms.ReplicatedCollateralIndividualDao;

public class NeutronElasticValidator {

  private final ReplicatedClientDao repClientDao;
  private final ReplicatedClientAddressDao repClientAddressDao;
  private final ReplicatedCollateralIndividualDao repCollateralIndividualDao;



  @Inject
  public NeutronElasticValidator(final ReplicatedClientDao repClientDao,
      final ReplicatedClientAddressDao repClientAddressDao,
      final ReplicatedCollateralIndividualDao repCollateralIndividualDao) {
    this.repClientDao = repClientDao;
    this.repClientAddressDao = repClientAddressDao;
    this.repCollateralIndividualDao = repCollateralIndividualDao;
  }

}
