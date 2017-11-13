package com.google.inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import gov.ca.cwds.jobs.Goddard;

public class NeutronGuiceModuleTest extends Goddard {

  public static class TestNeutronGuiceModule extends NeutronGuiceModule {

    @Override
    protected void configure() {}

  }

  NeutronGuiceModule target;

  @Override
  public void setup() throws Exception {
    super.setup();
    target = new TestNeutronGuiceModule();
  }

  @Test
  public void type() throws Exception {
    assertThat(NeutronGuiceModule.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void binder_Args__() throws Exception {
    Object actual = target.binder();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void binder_Args__2() throws Exception {
    Binder binder = mock(Binder.class);
    target.setTestBinder(binder);
    Object actual = target.binder();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getTestBinder_Args__() throws Exception {
    Object actual = target.getTestBinder();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setTestBinder_Args__Object() throws Exception {
    target.setTestBinder(mock(Binder.class));
  }

}
