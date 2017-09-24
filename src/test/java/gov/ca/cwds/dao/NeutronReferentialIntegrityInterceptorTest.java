package gov.ca.cwds.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.Iterator;

import org.hibernate.type.Type;
import org.junit.Test;

public class NeutronReferentialIntegrityInterceptorTest {

  @Test
  public void type() throws Exception {
    assertThat(NeutronReferentialIntegrityInterceptor.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    NeutronReferentialIntegrityInterceptor target = new NeutronReferentialIntegrityInterceptor();
    assertThat(target, notNullValue());
  }

  @Test
  public void onDelete_Args__Object__Serializable__ObjectArray__StringArray__TypeArray()
      throws Exception {
    NeutronReferentialIntegrityInterceptor target = new NeutronReferentialIntegrityInterceptor();
    Object entity = null;
    Serializable id = mock(Serializable.class);
    Object[] state = new Object[] {};
    String[] propertyNames = new String[] {};
    Type[] types = new Type[] {};
    target.onDelete(entity, id, state, propertyNames, types);
  }

  @Test
  public void onFlushDirty_Args__Object__Serializable__ObjectArray__ObjectArray__StringArray__TypeArray()
      throws Exception {
    NeutronReferentialIntegrityInterceptor target = new NeutronReferentialIntegrityInterceptor();
    Object entity = null;
    Serializable id = mock(Serializable.class);
    Object[] currentState = new Object[] {};
    Object[] previousState = new Object[] {};
    String[] propertyNames = new String[] {};
    Type[] types = new Type[] {};
    boolean actual =
        target.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void onLoad_Args__Object__Serializable__ObjectArray__StringArray__TypeArray()
      throws Exception {
    NeutronReferentialIntegrityInterceptor target = new NeutronReferentialIntegrityInterceptor();
    Object entity = null;
    Serializable id = mock(Serializable.class);
    Object[] state = new Object[] {};
    String[] propertyNames = new String[] {};
    Type[] types = new Type[] {};
    boolean actual = target.onLoad(entity, id, state, propertyNames, types);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void onSave_Args__Object__Serializable__ObjectArray__StringArray__TypeArray()
      throws Exception {
    NeutronReferentialIntegrityInterceptor target = new NeutronReferentialIntegrityInterceptor();
    Object entity = null;
    Serializable id = mock(Serializable.class);
    Object[] state = new Object[] {};
    String[] propertyNames = new String[] {};
    Type[] types = new Type[] {};
    boolean actual = target.onSave(entity, id, state, propertyNames, types);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void preFlush_Args__Iterator() throws Exception {
    NeutronReferentialIntegrityInterceptor target = new NeutronReferentialIntegrityInterceptor();
    Iterator iterator = mock(Iterator.class);
    target.preFlush(iterator);
  }

  @Test
  public void postFlush_Args__Iterator() throws Exception {
    NeutronReferentialIntegrityInterceptor target = new NeutronReferentialIntegrityInterceptor();
    Iterator iterator = mock(Iterator.class);
    target.postFlush(iterator);
  }

}
