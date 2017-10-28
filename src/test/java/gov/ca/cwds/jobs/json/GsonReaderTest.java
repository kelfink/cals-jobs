package gov.ca.cwds.jobs.json;

// import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.data.persistence.cms.EsPersonReferral;

public class GsonReaderTest {

  GsonReader target;
  Class<?> type;
  Type genericType;
  Annotation[] antns;
  MediaType mt;

  @Before
  public void setup() throws Exception {
    type = EsPersonReferral.class;
    genericType = mock(Type.class);
    antns = new Annotation[] {};
    mt = mock(MediaType.class);

    target = new GsonReader();
  }

  @Test
  public void type() throws Exception {
    assertThat(GsonReader.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    GsonReader target = new GsonReader();
    assertThat(target, notNullValue());
  }

  @Test
  public void isReadable_Args__Class__Type__AnnotationArray__MediaType() throws Exception {
    boolean actual = target.isReadable(type, genericType, antns, mt);
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void readFrom_Args__Class__Type__AnnotationArray__MediaType__MultivaluedMap__InputStream()
      throws Exception {
    MultivaluedMap<String, String> mm = mock(MultivaluedMap.class);

    // fixture("fixtures/domain/legacy/Allegation/valid/abuseFrequencyPeriodCodeD.json");

    InputStream in = mock(InputStream.class);
    Object actual = target.readFrom(type, genericType, antns, mt, mm, in);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test(expected = IOException.class)
  public void readFrom_Args__Class__Type__AnnotationArray__MediaType__MultivaluedMap__InputStream_T__IOException()
      throws Exception {
    MultivaluedMap<String, String> mm = mock(MultivaluedMap.class);
    InputStream in = mock(InputStream.class);

    target.readFrom(type, genericType, antns, mt, mm, in);
  }

}

