package gov.ca.cwds.neutron.rocket.syscode;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.persistence.PersistentObject;

//
// ============================================================================
// System codes persistence class for new system
// ============================================================================
//
/**
 * System codes persistence class for new system.
 */
@Entity
@Table(name = "system_codes")
public class NsSystemCode implements PersistentObject {

  private static final long serialVersionUID = 8370500764130606101L;

  @Id
  @Column(name = "ID")
  @Type(type = "int")
  private Integer id;

  @Column(name = "CATEGORY_ID")
  private String categoryId;

  @Column(name = "SUB_CATEGORY_ID")
  @Type(type = "int")
  private Integer subCategoryId;

  @Column(name = "DESCRIPTION")
  @ColumnTransformer(read = "trim(DESCRIPTION)")
  private String description;

  @Column(name = "CATEGORY_DESCRIPTION")
  @ColumnTransformer(read = "trim(CATEGORY_DESCRIPTION)")
  private String categoryDescription;

  @Column(name = "SUB_CATEGORY_DESCRIPTION")
  @ColumnTransformer(read = "trim(SUB_CATEGORY_DESCRIPTION)")
  private String subCategoryDescription;

  @Column(name = "OTHER_CODE")
  @ColumnTransformer(read = "trim(OTHER_CODE)")
  private String otherCode;

  @Column(name = "LOGICAL_ID")
  @ColumnTransformer(read = "trim(LOGICAL_ID)")
  private String logicalId;

  /**
   * Default constructor.
   */
  public NsSystemCode() {
    // Default constructor
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getSubCategoryId() {
    return subCategoryId;
  }

  public void setSubCategoryId(Integer subCategoryId) {
    this.subCategoryId = subCategoryId;
  }

  public String getCategoryDescription() {
    return categoryDescription;
  }

  public void setCategoryDescription(String categoryDescription) {
    this.categoryDescription = categoryDescription;
  }

  public String getSubCategoryDescription() {
    return subCategoryDescription;
  }

  public void setSubCategoryDescription(String subCategoryDescription) {
    this.subCategoryDescription = subCategoryDescription;
  }

  public String getOtherCode() {
    return otherCode;
  }

  public void setOtherCode(String otherCode) {
    this.otherCode = otherCode;
  }

  public String getLogicalId() {
    return logicalId;
  }

  public void setLogicalId(String logicalId) {
    this.logicalId = logicalId;
  }

  @Override
  public Serializable getPrimaryKey() {
    return getId();
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

}
