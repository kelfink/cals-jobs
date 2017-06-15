package gov.ca.cwds.data.model.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

public class DocumentMetadataTest {

  @Test
  public void type() throws Exception {
    assertThat(DocumentMetadata.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    DocumentMetadata target = new DocumentMetadata();
    assertThat(target, notNullValue());
  }

  @Test
  public void getHandle_Args__() throws Exception {
    DocumentMetadata target = new DocumentMetadata();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getHandle();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getStatus_Args__() throws Exception {
    DocumentMetadata target = new DocumentMetadata();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getStatus();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastUpdatedTimestamp_Args__() throws Exception {
    DocumentMetadata target = new DocumentMetadata();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getLastUpdatedTimestamp();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
