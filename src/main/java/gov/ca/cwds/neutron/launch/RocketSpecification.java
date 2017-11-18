package gov.ca.cwds.neutron.launch;

public class RocketSpecification {

  private String initialLoadView;

  private String initialLoadMQT;

  private String prepLastChangeSQL;

  private boolean initialLoadUseJdbc;

  private boolean initialLoadUseTransformThread;

  private boolean viewNormalizer;

  private boolean deleteLimitedAccessRecords;

  private String nestedElementName;

  private boolean documentRoot;

  private boolean validateDocument;

  private String primaryKeyColumn;

  private String legacySourceTable;

  private Class<?> denormalizedClass;

  private Class<?> normalizedClass;

}
