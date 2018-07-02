package gov.ca.cwds.jobs.common.batch;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class JobBatch<S extends SavePoint> {

  private List<ChangedEntityIdentifier<S>> identifiers;

  public JobBatch(List<ChangedEntityIdentifier<S>> identifiers) {
    this.identifiers = identifiers;
  }

  public List<ChangedEntityIdentifier<S>> getChangedEntityIdentifiers() {
    return identifiers;
  }

  public void addIdentifier(ChangedEntityIdentifier<S> identifier) {
    this.identifiers.add(identifier);
  }

  public void addIdentifiers(List<ChangedEntityIdentifier<S>> identifiers) {
    this.identifiers.addAll(identifiers);
  }

  public void setChangedEntityIdentifiers(List<ChangedEntityIdentifier<S>> changedEntities) {
    this.identifiers = changedEntities;
  }

  public int getSize() {
    return identifiers.size();
  }

  public boolean isEmpty() {
    return identifiers.isEmpty();
  }

  @Override
  public String toString() {
    return "batch size = " + identifiers.size();
  }

}
