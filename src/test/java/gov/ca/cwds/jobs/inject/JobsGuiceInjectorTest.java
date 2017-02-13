package gov.ca.cwds.jobs.inject;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class JobsGuiceInjectorTest {

  @Test
  public void type() throws Exception {
    assertNotNull(JobsGuiceInjector.class);
  }

  @Test
  public void instantiation() throws Exception {
    final JobsGuiceInjector target = new JobsGuiceInjector();
    assertNotNull(target);
  }

  // @Test
  // public void configure_Args__() throws Exception {
  // JobsGuiceInjector target = new JobsGuiceInjector();
  //
  // final Binder binder = Mockito.mock(Binder.class);
  // final AnnotatedBindingBuilder<?> annoBuilder = Mockito.mock(AnnotatedBindingBuilder.class);
  // when(binder.bind(Matchers.<Class<A>>any())).thenReturn(annoBuilder);
  // }

  // @Test
  // public void elasticsearchClient_Args__() throws Exception {
  // JobsGuiceInjector target = new JobsGuiceInjector();
  // // given
  // // e.g. : given(mocked.called()).willReturn(1);
  // // when
  // final Client actual = target.elasticsearchClient();
  // // then
  // // e.g. : verify(mocked).called();
  // // Client expected = null;
  // assertTrue(actual instanceof ElasticsearchClient);
  // actual.close();
  // }

  // @Test
  // public void nsSessionFactory_Args__() throws Exception {
  // JobsGuiceInjector target = new JobsGuiceInjector();
  // // given
  // // e.g. : given(mocked.called()).willReturn(1);
  // // when
  // SessionFactory actual = target.nsSessionFactory();
  // // then
  // // e.g. : verify(mocked).called();
  // // SessionFactory expected = null;
  // assertTrue(actual instanceof SessionFactory);
  // actual.close();
  //
  // target.elasticsearchClient().close();
  // }

}
