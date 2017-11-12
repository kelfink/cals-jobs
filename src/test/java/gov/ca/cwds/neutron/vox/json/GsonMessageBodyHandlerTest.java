package gov.ca.cwds.neutron.vox.json;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.neutron.vox.VoxCommandInstruction;
import gov.ca.cwds.neutron.vox.json.GsonMessageBodyHandler;

public class GsonMessageBodyHandlerTest {

  GsonMessageBodyHandler target;

  VoxCommandInstruction object;
  Class<VoxCommandInstruction> type;
  Type genericType;
  Annotation[] annotations;
  MediaType mediaType;
  MultivaluedMap<String, Object> httpHeaders;
  OutputStream entityStream;

  @Before
  public void setup() throws Exception {
    target = new GsonMessageBodyHandler();

    object = new VoxCommandInstruction("client", "stop");
    type = VoxCommandInstruction.class;
    genericType = VoxCommandInstruction.class;
    annotations = new Annotation[] {};
    mediaType = MediaType.APPLICATION_JSON_TYPE;
    httpHeaders = new MultivaluedHashMap<String, Object>();
    entityStream = System.out;
  }

  @Test
  public void type() throws Exception {
    assertThat(GsonMessageBodyHandler.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void isReadable_Args__Class__Type__javalangannotationAnnotationArray__MediaType()
      throws Exception {
    boolean actual = target.isReadable(type, genericType, annotations, mediaType);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void readFrom_Args__Class__Type__AnnotationArray__MediaType__MultivaluedMap__InputStream()
      throws Exception {
    final InputStream in = this.getClass().getResourceAsStream("/fixtures/test.json");
    final MultivaluedMap<String, String> httpHeaders = new MultivaluedHashMap<String, String>();

    final Class<Object> type = Object.class;
    final Object actual =
        target.readFrom(type, genericType, annotations, mediaType, httpHeaders, in);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void isWriteable_Args__Class__Type__AnnotationArray__MediaType() throws Exception {
    boolean actual = target.isWriteable(type, genericType, annotations, mediaType);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getSize_Args__Object__Class__Type__AnnotationArray__MediaType() throws Exception {
    long actual = target.getSize(object, type, genericType, annotations, mediaType);
    assertThat(actual, is(not(0L)));
  }

  @Test
  public void writeTo_Args__Object__Class__Type__AnnotationArray__MediaType__MultivaluedMap__OutputStream()
      throws Exception {
    target.writeTo(object, type, genericType, annotations, mediaType, httpHeaders, entityStream);
  }

}
