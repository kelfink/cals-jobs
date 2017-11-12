package gov.ca.cwds.neutron.vox.json;

// import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
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
import gov.ca.cwds.neutron.vox.json.GsonReader;

public class GsonReaderTest {

  GsonReader target;
  Type genericType;
  Annotation[] antns;
  MediaType mt;

  VoxCommandInstruction object;
  Class<VoxCommandInstruction> type;
  MediaType mediaType;
  MultivaluedMap<String, Object> httpHeaders;
  OutputStream entityStream;

  @Before
  public void setup() throws Exception {
    genericType = mock(Type.class);
    antns = new Annotation[] {};
    mt = mock(MediaType.class);

    object = new VoxCommandInstruction("client", "stop");
    type = VoxCommandInstruction.class;
    genericType = mock(Type.class);
    mediaType = MediaType.APPLICATION_JSON_TYPE;
    httpHeaders = new MultivaluedHashMap<String, Object>();
    entityStream = System.out;

    target = new GsonReader();
  }

  @Test
  public void type() throws Exception {
    assertThat(GsonReader.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void isReadable_Args__Class__Type__AnnotationArray__MediaType() throws Exception {
    boolean actual = target.isReadable(type, genericType, antns, mt);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  // @Ignore
  public void readFrom_Args__Class__Type__AnnotationArray__MediaType__MultivaluedMap__InputStream()
      throws Exception {
    MultivaluedMap<String, String> mm = mock(MultivaluedMap.class);

    final InputStream in = this.getClass().getResourceAsStream("/fixtures/test.json");
    Object actual = target.readFrom(type, genericType, antns, mt, mm, in);
    assertThat(actual, is(notNullValue()));
  }

  @Test(expected = IOException.class)
  public void readFrom_Args__Class__Type__AnnotationArray__MediaType__MultivaluedMap__InputStream_T__IOException()
      throws Exception {
    MultivaluedMap<String, String> mm = mock(MultivaluedMap.class);
    InputStream in = mock(InputStream.class);
    target.readFrom(type, genericType, antns, mt, mm, in);
  }

}
