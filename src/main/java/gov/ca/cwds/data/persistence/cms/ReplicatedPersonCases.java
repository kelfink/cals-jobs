package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonCase;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiPersonAware;

/**
 * Pseudo-normalized container for CMS person case.
 * 
 * @author CWDS API Team
 */
public class ReplicatedPersonCases implements PersistentObject, ApiPersonAware {

  private static final long serialVersionUID = -8746969311364544478L;

  private String focusChildId;
  private List<ElasticSearchPersonCase> esPersonCases = new ArrayList<ElasticSearchPersonCase>();

  public ReplicatedPersonCases(String focusChildId) {
    this.focusChildId = focusChildId;
  }

  public List<ElasticSearchPersonCase> geElasticSearchPersonCases() {
    return esPersonCases;
  }

  public void setElasticSearchPersonCases(List<ElasticSearchPersonCase> esPersonCases) {
    this.esPersonCases = esPersonCases;
  }

  public void addElasticSearchPersonCase(ElasticSearchPersonCase esPersonCase) {
    esPersonCases.add(esPersonCase);
  }

  @Override
  public Serializable getPrimaryKey() {
    return this.focusChildId;
  }

  @Override
  public Date getBirthDate() {
    return null;
  }

  @Override
  public String getFirstName() {
    return null;
  }

  @Override
  public String getGender() {
    return null;
  }

  @Override
  public String getLastName() {
    return null;
  }

  @Override
  public String getMiddleName() {
    return null;
  }

  @Override
  public String getNameSuffix() {
    return null;
  }

  @Override
  public String getSsn() {
    return null;
  }
}
