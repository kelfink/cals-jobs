package gov.ca.cwds.jobs.util.jdbc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.Before;
import org.junit.Test;

public class StringArrayTypeTest {

  private StringArrayType target = new StringArrayType();

  @Before
  public void setup() {
    target = new StringArrayType();
  }

  @Test
  public void type() throws Exception {
    assertThat(StringArrayType.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  // @Test
  public void sqlTypes_Args__() throws Exception {
    int[] actual = target.sqlTypes();
    int[] expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void returnedClass_Args__() throws Exception {
    Class actual = target.returnedClass();
    Class expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void equals_Args__Object__Object() throws Exception {
    Object x = null;
    Object y = null;
    boolean actual = target.equals(x, y);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void equals_Args__Object__Object_T__HibernateException() throws Exception {
    Object x = null;
    Object y = null;
    try {
      target.equals(x, y);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
    }
  }

  @Test
  public void hashCode_Args__Object() throws Exception {
    Object x = null;
    int actual = target.hashCode(x);
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void hashCode_Args__Object_T__HibernateException() throws Exception {
    Object x = null;
    try {
      target.hashCode(x);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
    }
  }

  @Test
  public void nullSafeGet_Args__ResultSet__StringArray__SharedSessionContractImplementor__Object()
      throws Exception {
    ResultSet rs = mock(ResultSet.class);
    String[] names = new String[] {};
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    Object owner = null;
    Object actual = target.nullSafeGet(rs, names, session, owner);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void nullSafeGet_Args__ResultSet__StringArray__SharedSessionContractImplementor__Object_T__HibernateException()
      throws Exception {
    ResultSet rs = mock(ResultSet.class);
    String[] names = new String[] {};
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    Object owner = null;
    try {
      target.nullSafeGet(rs, names, session, owner);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
    }
  }

  // @Test
  public void nullSafeGet_Args__ResultSet__StringArray__SharedSessionContractImplementor__Object_T__SQLException()
      throws Exception {
    ResultSet rs = mock(ResultSet.class);
    String[] names = new String[] {};
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    Object owner = null;
    try {
      target.nullSafeGet(rs, names, session, owner);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void nullSafeSet_Args__PreparedStatement__Object__int__SharedSessionContractImplementor()
      throws Exception {
    PreparedStatement st = mock(PreparedStatement.class);
    Object value = null;
    int index = 0;
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    target.nullSafeSet(st, value, index, session);
  }

  // @Test
  public void nullSafeSet_Args__PreparedStatement__Object__int__SharedSessionContractImplementor_T__HibernateException()
      throws Exception {
    PreparedStatement st = mock(PreparedStatement.class);
    Object value = null;
    int index = 0;
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    try {
      target.nullSafeSet(st, value, index, session);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
    }
  }

  // @Test
  public void nullSafeSet_Args__PreparedStatement__Object__int__SharedSessionContractImplementor_T__SQLException()
      throws Exception {
    PreparedStatement st = mock(PreparedStatement.class);
    Object value = null;
    int index = 0;
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    try {
      target.nullSafeSet(st, value, index, session);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void deepCopy_Args__Object() throws Exception {
    Object value = null;
    Object actual = target.deepCopy(value);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void deepCopy_Args__Object_T__HibernateException() throws Exception {
    Object value = null;
    try {
      target.deepCopy(value);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
    }
  }

  @Test
  public void isMutable_Args__() throws Exception {
    boolean actual = target.isMutable();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void disassemble_Args__Object() throws Exception {
    Object value = null;
    Serializable actual = target.disassemble(value);
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void disassemble_Args__Object_T__HibernateException() throws Exception {
    Object value = null;
    try {
      target.disassemble(value);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
    }
  }

  // @Test
  public void assemble_Args__Serializable__Object() throws Exception {
    Serializable cached = mock(Serializable.class);
    Object owner = null;
    Object actual = target.assemble(cached, owner);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void assemble_Args__Serializable__Object_T__HibernateException() throws Exception {
    Serializable cached = mock(Serializable.class);
    Object owner = null;
    try {
      target.assemble(cached, owner);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
    }
  }

  // @Test
  public void replace_Args__Object__Object__Object() throws Exception {
    Object original = null;
    Object target_ = null;
    Object owner = null;
    Object actual = target.replace(original, target_, owner);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void replace_Args__Object__Object__Object_T__HibernateException() throws Exception {
    Object original = null;
    Object target_ = null;
    Object owner = null;
    try {
      target.replace(original, target_, owner);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
    }
  }

}
