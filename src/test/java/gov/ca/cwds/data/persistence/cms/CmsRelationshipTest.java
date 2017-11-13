package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;

public class CmsRelationshipTest {

  @BeforeClass
  public static void setupClass() {
    SimpleTestSystemCodeCache.init();
  }

  @Test
  public void type() throws Exception {
    assertThat(CmsRelationship.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    Short relCode = (short) 196;
    CmsRelationship target = new CmsRelationship(relCode);
    assertThat(target, notNullValue());
  }

  @Test
  public void instantiation2() throws Exception {
    Short relCode = (short) 196;
    CmsRelationship target = new CmsRelationship(relCode);
    assertThat(target, notNullValue());
  }

  @Test
  public void getSysCodeId_Args__() throws Exception {
    Short relCode = (short) 196;
    CmsRelationship target = new CmsRelationship(relCode);
    short actual = target.getSysCodeId();
    short expected = (short) 196;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSysCodeId_Args__short() throws Exception {
    Short relCode = (short) 196;
    CmsRelationship target = new CmsRelationship(relCode);
    short sysCodeId = 0;
    target.setSysCodeId(sysCodeId);
  }

  @Test
  public void getPrimaryRel_Args__() throws Exception {
    Short relCode = (short) 196;
    CmsRelationship target = new CmsRelationship(relCode);
    String actual = target.getPrimaryRel();
    String expected = "Daughter";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setPrimaryRel_Args__String() throws Exception {
    Short relCode = (short) 196;
    CmsRelationship target = new CmsRelationship(relCode);
    String primaryRel = null;
    target.setPrimaryRel(primaryRel);
  }

  @Test
  public void getSecondaryRel_Args__() throws Exception {
    Short relCode = (short) 196;
    CmsRelationship target = new CmsRelationship(relCode);
    String actual = target.getSecondaryRel();
    String expected = "Mother";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSecondaryRel_Args__String() throws Exception {
    Short relCode = (short) 196;
    CmsRelationship target = new CmsRelationship(relCode);
    String secondaryRel = null;
    target.setSecondaryRel(secondaryRel);
  }

  @Test
  public void getRelContext_Args__() throws Exception {
    Short relCode = (short) 196;
    CmsRelationship target = new CmsRelationship(relCode);
    String actual = target.getRelContext();
    String expected = "Birth";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelContext_Args__String() throws Exception {
    Short relCode = (short) 196;
    CmsRelationship target = new CmsRelationship(relCode);
    String relContext = null;
    target.setRelContext(relContext);
  }

  @Test
  public void toString_Args__() throws Exception {
    Short relCode = (short) 196;
    CmsRelationship target = new CmsRelationship(relCode);
    String actual = target.toString();
    assertThat(actual, notNullValue());
  }

}
