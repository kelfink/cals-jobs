package gov.ca.cwds.jobs.service;

import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedClientAddressDao;
import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.dao.cms.ReplicatedCollateralIndividualDao;
import gov.ca.cwds.dao.cms.ReplicatedOtherClientNameDao;
import gov.ca.cwds.dao.cms.ReplicatedReporterDao;

public class NeutronElasticValidator {

  private final ReplicatedClientDao repClientDao;
  private final ReplicatedClientAddressDao repClientAddressDao;
  private final ReplicatedCollateralIndividualDao repCollateralIndividualDao;

  private final ReplicatedReporterDao repReporterDao;
  private final ReplicatedOtherClientNameDao repOtherClientNameDao;

  @Inject
  public NeutronElasticValidator(final ReplicatedClientDao repClientDao,
      final ReplicatedClientAddressDao repClientAddressDao,
      final ReplicatedCollateralIndividualDao repCollateralIndividualDao,
      final ReplicatedReporterDao repReporterDao,
      final ReplicatedOtherClientNameDao repOtherClientNameDao) {
    this.repClientDao = repClientDao;
    this.repClientAddressDao = repClientAddressDao;
    this.repCollateralIndividualDao = repCollateralIndividualDao;
    this.repReporterDao = repReporterDao;
    this.repOtherClientNameDao = repOtherClientNameDao;
  }

}
