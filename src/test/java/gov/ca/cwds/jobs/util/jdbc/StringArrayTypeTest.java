package gov.ca.cwds.jobs.util.jdbc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.jobs.Goddard;

public class StringArrayTypeTest extends Goddard {

  private StringArrayType target = new StringArrayType();

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

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
    final String[] names = new String[] {"1", "2", "3"};
    when(rs.next()).thenReturn(true, true, false);

    final Array arr = mock(Array.class);
    when(rs.getArray(any(String.class))).thenReturn(arr);

    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    Object owner = null;
    Object actual = target.nullSafeGet(rs, names, session, owner);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
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

  @Test
  public void nullSafeSet_2() throws Exception {
    PreparedStatement st = mock(PreparedStatement.class);
    String[] whatever = {"well, isn't that special"};
    Object value = whatever;
    int index = 0;
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    java.sql.Connection con = mock(java.sql.Connection.class);
    java.sql.Array arr = mock(java.sql.Array.class);

    when(session.connection()).thenReturn(con);
    when(con.createArrayOf(any(String.class), any(String[].class))).thenReturn(arr);

    target.nullSafeSet(st, value, index, session);
  }

  @Test
  public void deepCopy_Args__Object() throws Exception {
    Object value = null;
    Object actual = target.deepCopy(value);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
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

  @Test
  public void replace_Args__Object__Object__Object() throws Exception {
    Object original = null;
    Object target_ = null;
    Object owner = null;
    Object actual = target.replace(original, target_, owner);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void assemble_Args__Serializable__Object() throws Exception {
    StringArrayType target = new StringArrayType();
    Serializable cached = "test";
    Object owner = null;
    Object actual = target.assemble(cached, owner);
    assertThat(actual, is(notNullValue()));
  }

}
