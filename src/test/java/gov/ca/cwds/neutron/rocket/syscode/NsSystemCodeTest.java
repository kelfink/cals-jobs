package gov.ca.cwds.neutron.rocket.syscode;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.Test;

import gov.ca.cwds.neutron.rocket.syscode.NsSystemCode;

public class NsSystemCodeTest {

  @Test
  public void type() throws Exception {
    assertThat(NsSystemCode.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    NsSystemCode target = new NsSystemCode();
    assertThat(target, notNullValue());
  }

  @Test
  public void getId_Args__() throws Exception {
    NsSystemCode target = new NsSystemCode();
    Integer actual = target.getId();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setId_Args__Integer() throws Exception {
    NsSystemCode target = new NsSystemCode();
    Integer id = null;
    target.setId(id);
  }

  @Test
  public void getCategoryId_Args__() throws Exception {
    NsSystemCode target = new NsSystemCode();
    String actual = target.getCategoryId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCategoryId_Args__String() throws Exception {
    NsSystemCode target = new NsSystemCode();
    String categoryId = null;
    target.setCategoryId(categoryId);
  }

  @Test
  public void getDescription_Args__() throws Exception {
    NsSystemCode target = new NsSystemCode();
    String actual = target.getDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setDescription_Args__String() throws Exception {
    NsSystemCode target = new NsSystemCode();
    String description = null;
    target.setDescription(description);
  }

  @Test
  public void getSubCategoryId_Args__() throws Exception {
    NsSystemCode target = new NsSystemCode();
    Integer actual = target.getSubCategoryId();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSubCategoryId_Args__Integer() throws Exception {
    NsSystemCode target = new NsSystemCode();
    Integer subCategoryId = null;
    target.setSubCategoryId(subCategoryId);
  }

  @Test
  public void getCategoryDescription_Args__() throws Exception {
    NsSystemCode target = new NsSystemCode();
    String actual = target.getCategoryDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCategoryDescription_Args__String() throws Exception {
    NsSystemCode target = new NsSystemCode();
    String categoryDescription = null;
    target.setCategoryDescription(categoryDescription);
  }

  @Test
  public void getSubCategoryDescription_Args__() throws Exception {
    NsSystemCode target = new NsSystemCode();
    String actual = target.getSubCategoryDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSubCategoryDescription_Args__String() throws Exception {
    NsSystemCode target = new NsSystemCode();
    String subCategoryDescription = null;
    target.setSubCategoryDescription(subCategoryDescription);
  }

  @Test
  public void getOtherCode_Args__() throws Exception {
    NsSystemCode target = new NsSystemCode();
    String actual = target.getOtherCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setOtherCode_Args__String() throws Exception {
    NsSystemCode target = new NsSystemCode();
    String otherCode = null;
    target.setOtherCode(otherCode);
  }

  @Test
  public void getLogicalId_Args__() throws Exception {
    NsSystemCode target = new NsSystemCode();
    String actual = target.getLogicalId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLogicalId_Args__String() throws Exception {
    NsSystemCode target = new NsSystemCode();
    String logicalId = null;
    target.setLogicalId(logicalId);
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    NsSystemCode target = new NsSystemCode();
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void hashCode_Args__() throws Exception {
    NsSystemCode target = new NsSystemCode();
    int actual = target.hashCode();
    int expected = 0;
    assertThat(actual, is(not(expected)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    NsSystemCode target = new NsSystemCode();
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

}
