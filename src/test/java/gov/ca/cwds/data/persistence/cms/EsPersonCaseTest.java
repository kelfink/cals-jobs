package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;
import java.util.Map;

import org.junit.Test;

public class EsPersonCaseTest {

  private static final class TestTarget extends EsPersonCase {

    private String id;

    private TestTarget() {
      super();
    }

    private TestTarget(String id) {
      super();
      this.id = id;
    }

    @Override
    public String getNormalizationGroupKey() {
      return id;
    }

    @Override
    public ReplicatedPersonCases normalize(Map<Object, ReplicatedPersonCases> map) {
      return new ReplicatedPersonCases(this.id);
    }

  }

  @Test
  public void type() throws Exception {
    assertThat(EsPersonCase.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    EsPersonCase target = new TestTarget("abc12340x3");
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    EsPersonCase target = new TestTarget("abc12340x3");
    Class<ReplicatedPersonCases> actual = target.getNormalizationClass();
    Class<ReplicatedPersonCases> expected = ReplicatedPersonCases.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    EsPersonCase target = new TestTarget("abc12340x3");
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
