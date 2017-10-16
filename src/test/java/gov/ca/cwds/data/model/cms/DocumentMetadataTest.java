package gov.ca.cwds.data.model.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class DocumentMetadataTest {
  DocumentMetadata target = new DocumentMetadata();

  @Before
  public void setup() throws Exception {
    target = new DocumentMetadata();
  }

  @Test
  public void type() throws Exception {
    assertThat(DocumentMetadata.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getHandle_Args__() throws Exception {
    String actual = target.getHandle();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getStatus_Args__() throws Exception {
    String actual = target.getStatus();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastUpdatedTimestamp_Args__() throws Exception {
    Date actual = target.getLastUpdatedTimestamp();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
