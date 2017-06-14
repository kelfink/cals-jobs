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
import org.junit.Test;

public class StringArrayTypeTest {

  @Test
  public void type() throws Exception {
    assertThat(StringArrayType.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    StringArrayType target = new StringArrayType();
    assertThat(target, notNullValue());
  }

  // @Test
  public void sqlTypes_Args__() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int[] actual = target.sqlTypes();
    // then
    // e.g. : verify(mocked).called();
    int[] expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void returnedClass_Args__() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Class actual = target.returnedClass();
    // then
    // e.g. : verify(mocked).called();
    Class expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void equals_Args__Object__Object() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    Object x = null;
    Object y = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    boolean actual = target.equals(x, y);
    // then
    // e.g. : verify(mocked).called();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void equals_Args__Object__Object_T__HibernateException() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    Object x = null;
    Object y = null;
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.equals(x, y);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
      // then
    }
  }

  @Test
  public void hashCode_Args__Object() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    Object x = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int actual = target.hashCode(x);
    // then
    // e.g. : verify(mocked).called();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void hashCode_Args__Object_T__HibernateException() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    Object x = null;
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.hashCode(x);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
      // then
    }
  }

  @Test
  public void nullSafeGet_Args__ResultSet__StringArray__SharedSessionContractImplementor__Object()
      throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    ResultSet rs = mock(ResultSet.class);
    String[] names = new String[] {};
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    Object owner = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Object actual = target.nullSafeGet(rs, names, session, owner);
    // then
    // e.g. : verify(mocked).called();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void nullSafeGet_Args__ResultSet__StringArray__SharedSessionContractImplementor__Object_T__HibernateException()
      throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    ResultSet rs = mock(ResultSet.class);
    String[] names = new String[] {};
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    Object owner = null;
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.nullSafeGet(rs, names, session, owner);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
      // then
    }
  }

  // @Test
  public void nullSafeGet_Args__ResultSet__StringArray__SharedSessionContractImplementor__Object_T__SQLException()
      throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    ResultSet rs = mock(ResultSet.class);
    String[] names = new String[] {};
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    Object owner = null;
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.nullSafeGet(rs, names, session, owner);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
      // then
    }
  }

  @Test
  public void nullSafeSet_Args__PreparedStatement__Object__int__SharedSessionContractImplementor()
      throws Exception {

    StringArrayType target = new StringArrayType();
    // given
    PreparedStatement st = mock(PreparedStatement.class);
    Object value = null;
    int index = 0;
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.nullSafeSet(st, value, index, session);
    // then
    // e.g. : verify(mocked).called();
  }

  // @Test
  public void nullSafeSet_Args__PreparedStatement__Object__int__SharedSessionContractImplementor_T__HibernateException()
      throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    PreparedStatement st = mock(PreparedStatement.class);
    Object value = null;
    int index = 0;
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.nullSafeSet(st, value, index, session);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
      // then
    }
  }

  // @Test
  public void nullSafeSet_Args__PreparedStatement__Object__int__SharedSessionContractImplementor_T__SQLException()
      throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    PreparedStatement st = mock(PreparedStatement.class);
    Object value = null;
    int index = 0;
    SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.nullSafeSet(st, value, index, session);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
      // then
    }
  }

  @Test
  public void deepCopy_Args__Object() throws Exception {

    StringArrayType target = new StringArrayType();
    // given
    Object value = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Object actual = target.deepCopy(value);
    // then
    // e.g. : verify(mocked).called();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void deepCopy_Args__Object_T__HibernateException() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    Object value = null;
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.deepCopy(value);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
      // then
    }
  }

  @Test
  public void isMutable_Args__() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    boolean actual = target.isMutable();
    // then
    // e.g. : verify(mocked).called();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void disassemble_Args__Object() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    Object value = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Serializable actual = target.disassemble(value);
    // then
    // e.g. : verify(mocked).called();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void disassemble_Args__Object_T__HibernateException() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    Object value = null;
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.disassemble(value);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
      // then
    }
  }

  // @Test
  public void assemble_Args__Serializable__Object() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    Serializable cached = mock(Serializable.class);
    Object owner = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Object actual = target.assemble(cached, owner);
    // then
    // e.g. : verify(mocked).called();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void assemble_Args__Serializable__Object_T__HibernateException() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    Serializable cached = mock(Serializable.class);
    Object owner = null;
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.assemble(cached, owner);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
      // then
    }
  }

  // @Test
  public void replace_Args__Object__Object__Object() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    Object original = null;
    Object target_ = null;
    Object owner = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Object actual = target.replace(original, target_, owner);
    // then
    // e.g. : verify(mocked).called();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void replace_Args__Object__Object__Object_T__HibernateException() throws Exception {
    StringArrayType target = new StringArrayType();
    // given
    Object original = null;
    Object target_ = null;
    Object owner = null;
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.replace(original, target_, owner);
      fail("Expected exception was not thrown!");
    } catch (HibernateException e) {
      // then
    }
  }

}
