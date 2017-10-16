package gov.ca.cwds.dao.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.StringType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import gov.ca.cwds.data.model.cms.DocumentMetadata;
import gov.ca.cwds.jobs.PersonJobTester;

public class DocumentMetadataDaoImplTest extends PersonJobTester {

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
  @Ignore
  public void findByLastJobRunTimeMinusOneMinute_Args__Date() throws Exception {
    SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
    Session session = Mockito.mock(Session.class);
    final Query query = Mockito.mock(Query.class);

    when(sessionFactory.getCurrentSession()).thenReturn(session);
    when(session.getNamedQuery(any())).thenReturn(query);
    when(query.list()).thenReturn(new ArrayList<>());
    when(query.setParameter(any(String.class), any(String.class), any(StringType.class)))
        .thenReturn(query);

    final List<DocumentMetadata> actual = target.findByLastJobRunTimeMinusOneMinute(new Date());
    final List<DocumentMetadata> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
