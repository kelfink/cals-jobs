package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EsPersonTypeTest {

  @Test
  public void testType() throws Exception {
    assertThat(EsPersonType.class, notNullValue());
  }

  @Test
  public void test_valueof() throws Exception {
    EsPersonType actual = EsPersonType.valueOf("REPORTER");
    EsPersonType expected = EsPersonType.REPORTER;
    assertThat(actual, is(equalTo(expected)));
  }

}
