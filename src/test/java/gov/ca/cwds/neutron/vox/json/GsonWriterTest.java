package gov.ca.cwds.neutron.vox.json;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.neutron.vox.VoxCommandInstruction;
import gov.ca.cwds.neutron.vox.json.GsonWriter;

public class GsonWriterTest {

  GsonWriter target;

  @Before
  public void setup() throws Exception {
    target = new GsonWriter();
  }

  @Test
  public void type() throws Exception {
    assertThat(GsonWriter.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void writeTo_Args__Object__Class__Type__AnnotationArray__MediaType__MultivaluedMap__OutputStream()
      throws Exception {
    VoxCommandInstruction t = new VoxCommandInstruction("client", "stop");
    Class<?> type = VoxCommandInstruction.class;
    Type genericType = mock(Type.class);
    Annotation[] annotations = new Annotation[] {};
    MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
    MultivaluedMap<String, Object> httpHeaders = new MultivaluedHashMap<String, Object>();
    OutputStream entityStream = System.out;
    target.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
  }

  // @Test(expected = IOException.class)
  // public void
  // writeTo_Args__Object__Class__Type__AnnotationArray__MediaType__MultivaluedMap__OutputStream_T__IOException()
  // throws Exception {
  // NeutronJobManagementBean t = new NeutronJobManagementBean("client", "stop", "crap");
  // Class<?> type = NeutronJobManagementBean.class;
  // Type genericType = mock(Type.class);
  // Annotation[] annotations = new Annotation[] {};
  // MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
  // MultivaluedMap<String, Object> httpHeaders = new MultivaluedHashMap<String, Object>();
  // OutputStream entityStream = System.out;
  // target.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
  // }

  @Test
  public void getSize_Args__Object__Class__Type__AnnotationArray__MediaType() throws Exception {
    VoxCommandInstruction t = new VoxCommandInstruction("client", "stop");
    Class<?> type = VoxCommandInstruction.class;
    Type genericType = mock(Type.class);
    Annotation[] annotations = new Annotation[] {};
    MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
    MultivaluedMap<String, Object> httpHeaders = new MultivaluedHashMap<String, Object>();
    OutputStream entityStream = System.out;
    target.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    long actual = target.getSize(t, type, genericType, annotations, mediaType);
    assertThat(actual, is(not(0L)));
  }

  @Test
  public void isWriteable_Args__Class__Type__AnnotationArray__MediaType() throws Exception {
    VoxCommandInstruction t = new VoxCommandInstruction("client", "stop");
    Class<?> type = VoxCommandInstruction.class;
    Type genericType = mock(Type.class);
    Annotation[] annotations = new Annotation[] {};
    MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
    MultivaluedMap<String, Object> httpHeaders = new MultivaluedHashMap<String, Object>();
    OutputStream entityStream = System.out;
    target.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    boolean actual = target.isWriteable(type, genericType, annotations, mediaType);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

}
