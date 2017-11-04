package gov.ca.cwds.dao.cms;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import gov.ca.cwds.data.model.cms.DocumentMetadata;
import gov.ca.cwds.jobs.Goddard;

public class DocumentMetadataDaoImplTest extends Goddard {

  DocumentMetadataDaoImpl target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new DocumentMetadataDaoImpl(sessionFactory);
  }

  @Test
  public void type() throws Exception {
    assertThat(DocumentMetadataDaoImpl.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
    assertThat(target, notNullValue());
  }

  @Test
  public void findByLastJobRunTimeMinusOneMinute_Args__Date() throws Exception {
    final List<DocumentMetadata> actual = target.findByLastJobRunTimeMinusOneMinute(new Date());
    assertThat(actual, is(notNullValue()));
  }

}
