package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.Lists;

import gov.ca.cwds.data.es.ElasticSearchPersonCase;
import gov.ca.cwds.data.es.ElasticSearchPersonParent;
import gov.ca.cwds.neutron.util.shrinkray.RetrovillePerson;

/**
 * Pseudo-normalized container for CMS person case.
 * 
 * @author CWDS API Team
 */
public class ReplicatedPersonCases implements RetrovillePerson {

  private static final long serialVersionUID = -8746969311364544478L;

  private String groupId;

  /**
   * Key: Case ID <br>
   * Value: ElasticSearchPersonCase objects for the keyed case id.
   */
  private Map<String, ElasticSearchPersonCase> personCases = new LinkedHashMap<>();

  /**
   * Key: Case ID <br>
   * Value: ElasticSearchPersonParent objects for the keyed case id.
   */
  private Map<String, List<ElasticSearchPersonParent>> caseParents = new LinkedHashMap<>();

  /**
   * Construct the object
   * 
   * @param groupId The group id (usually focus child or parent id)
   */
  public ReplicatedPersonCases(String groupId) {
    this.groupId = groupId;
  }

  /**
   * Get cases.
   * 
   * @return All ElasticSearchPersonCase objects.
   */
  public List<ElasticSearchPersonCase> getCases() {
    return Lists.newArrayList(personCases.values());
  }

  /**
   * Add case and parent to given case.
   * 
   * @param personCase Case to add.
   * @param caseParent Parent to add.
   */
  public void addCase(ElasticSearchPersonCase personCase, ElasticSearchPersonParent caseParent) {
    personCases.put(personCase.getId(), personCase);

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
    return this.groupId;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE, true);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }

  public String getGroupId() {
    return groupId;
  }

}
