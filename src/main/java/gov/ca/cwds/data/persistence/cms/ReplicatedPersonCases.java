package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonCase;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonParent;
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
  private List<ElasticSearchPersonCase> personCases = new ArrayList<ElasticSearchPersonCase>();

  /**
   * Key: Case ID <br>
   * Value: ElasticSearchPersonParent objects for the keyed case id.
   */
  private Map<String, List<ElasticSearchPersonParent>> caseParents = new HashMap<>();

  /**
   * Construct the object
   * 
   * @param focusChildId The child id (this is same as client id of the child)
   */
  public ReplicatedPersonCases(String focusChildId) {
    this.focusChildId = focusChildId;
  }

  /**
   * Get cases.
   * 
   * @return All ElasticSearchPersonCase objects.
   */
  public List<ElasticSearchPersonCase> getCases() {
    return personCases;
  }

  /**
   * Add case and parent to given case.
   * 
   * @param personCase Case to add.
   * @param caseParent Parent to add.
   */
  public void addCase(ElasticSearchPersonCase personCase, ElasticSearchPersonParent caseParent) {
    personCases.add(personCase);

    // Add parent
    if (caseParent != null) {
      List<ElasticSearchPersonParent> parents = caseParents.get(personCase.getId());
      if (parents == null) {
        parents = new ArrayList<>();
        caseParents.put(personCase.getId(), parents);
      }
      parents.add(caseParent);
      personCase.setParents(parents);
    }
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
