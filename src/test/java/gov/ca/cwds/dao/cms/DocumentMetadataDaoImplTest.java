package gov.ca.cwds.dao.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import gov.ca.cwds.data.model.cms.DocumentMetadata;

public class DocumentMetadataDaoImplTest {

  @Test
  public void type() throws Exception {
    assertThat(DocumentMetadataDaoImpl.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
    DocumentMetadataDaoImpl target = new DocumentMetadataDaoImpl(sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test
  @Ignore
  public void findByLastJobRunTimeMinusOneMinute_Args__Date() throws Exception {
    SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
    Session session = Mockito.mock(Session.class);
    Query query = Mockito.mock(Query.class);

    when(sessionFactory.getCurrentSession()).thenReturn(session);
    when(session.getNamedQuery(any())).thenReturn(query);

    DocumentMetadataDaoImpl target = new DocumentMetadataDaoImpl(sessionFactory);
    Date lastJobRunTime = new Date();
    List<DocumentMetadata> actual = target.findByLastJobRunTimeMinusOneMinute(lastJobRunTime);
    List<DocumentMetadata> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
