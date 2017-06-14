package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CmsReplicationOperationTest {

  @Test
  public void type() throws Exception {
    assertThat(CmsReplicationOperation.class, notNullValue());
  }

  @Test
  public void strToRepOp_Args__String_null_input() throws Exception {
    String op = null;
    CmsReplicationOperation actual = CmsReplicationOperation.strToRepOp(op);
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void test_valueof() throws Exception {
    CmsReplicationOperation actual = CmsReplicationOperation.valueOf("U");
    CmsReplicationOperation expected = CmsReplicationOperation.U;
    assertThat(actual, is(equalTo(expected)));
  }

}
