package gov.ca.cwds.neutron.launch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;

public class RocketSpecificationTest {

  RocketSpecification target;

  @Before
  public void setup() throws Exception {
    target = new RocketSpecification();
  }

  @Test
  public void type() throws Exception {
    assertThat(RocketSpecification.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getInitialLoadView_Args__() throws Exception {
    String actual = target.getInitialLoadView();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setInitialLoadView_Args__String() throws Exception {
    String initialLoadView = null;
    target.setInitialLoadView(initialLoadView);
  }

  @Test
  public void getInitialLoadMQT_Args__() throws Exception {
    String actual = target.getInitialLoadMQT();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setInitialLoadMQT_Args__String() throws Exception {
    String initialLoadMQT = null;
    target.setInitialLoadMQT(initialLoadMQT);
  }

  @Test
  public void getPrepLastChangeSQL_Args__() throws Exception {
    String actual = target.getPrepLastChangeSQL();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPrepLastChangeSQL_Args__String() throws Exception {
    String prepLastChangeSQL = null;
    target.setPrepLastChangeSQL(prepLastChangeSQL);
  }

  @Test
  public void isInitialLoadUseJdbc_Args__() throws Exception {
    boolean actual = target.isInitialLoadUseJdbc();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setInitialLoadUseJdbc_Args__boolean() throws Exception {
    boolean initialLoadUseJdbc = false;
    target.setInitialLoadUseJdbc(initialLoadUseJdbc);
  }

  @Test
  public void isInitialLoadUseTransformThread_Args__() throws Exception {
    boolean actual = target.isInitialLoadUseTransformThread();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setInitialLoadUseTransformThread_Args__boolean() throws Exception {
    boolean initialLoadUseTransformThread = false;
    target.setInitialLoadUseTransformThread(initialLoadUseTransformThread);
  }

  @Test
  public void isViewNormalizer_Args__() throws Exception {
    boolean actual = target.isViewNormalizer();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setViewNormalizer_Args__boolean() throws Exception {
    boolean viewNormalizer = false;
    target.setViewNormalizer(viewNormalizer);
  }

  @Test
  public void isDeleteLimitedAccessRecords_Args__() throws Exception {
    boolean actual = target.isDeleteLimitedAccessRecords();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setDeleteLimitedAccessRecords_Args__boolean() throws Exception {
    boolean deleteLimitedAccessRecords = false;
    target.setDeleteLimitedAccessRecords(deleteLimitedAccessRecords);
  }

  @Test
  public void getNestedElementName_Args__() throws Exception {
    String actual = target.getNestedElementName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setNestedElementName_Args__String() throws Exception {
    String nestedElementName = null;
    target.setNestedElementName(nestedElementName);
  }

  @Test
  public void isDocumentRoot_Args__() throws Exception {
    boolean actual = target.isDocumentRoot();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setDocumentRoot_Args__boolean() throws Exception {
    boolean documentRoot = false;
    target.setDocumentRoot(documentRoot);
  }

  @Test
  public void isValidateDocument_Args__() throws Exception {
    boolean actual = target.isValidateDocument();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setValidateDocument_Args__boolean() throws Exception {
    boolean validateDocument = false;
    target.setValidateDocument(validateDocument);
  }

  @Test
  public void getPrimaryKeyColumn_Args__() throws Exception {
    String actual = target.getPrimaryKeyColumn();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPrimaryKeyColumn_Args__String() throws Exception {
    String primaryKeyColumn = null;
    target.setPrimaryKeyColumn(primaryKeyColumn);
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    String actual = target.getLegacySourceTable();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLegacySourceTable_Args__String() throws Exception {
    String legacySourceTable = null;
    target.setLegacySourceTable(legacySourceTable);
  }

  @Test
  public void setDenormalizedClass_Args__Class() throws Exception {
    Class<?> denormalizedClass = TestDenormalizedEntity.class;
    target.setDenormalizedClass(denormalizedClass);
  }

  @Test
  public void setNormalizedClass_Args__Class() throws Exception {
    Class<?> normalizedClass = TestNormalizedEntity.class;
    target.setNormalizedClass(normalizedClass);
  }

}
