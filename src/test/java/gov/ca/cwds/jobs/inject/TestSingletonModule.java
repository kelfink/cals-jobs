package gov.ca.cwds.jobs.inject;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class TestSingletonModule extends AbstractModule {

  private static final String KEY_MSG = "my.test.string";

  @Override
  protected void configure() {
    final Properties p = new Properties();
    p.setProperty(KEY_MSG, "Some String"); // works with boolean, int, double ....
    Names.bindProperties(binder(), p);

    bind(X.class).to(TestMessenger.class).in(Singleton.class);
  }

  @FunctionalInterface
  public interface X {
    void spitItOutAlready();
  }

  // Without the @Singleton annotation, Guice doesn't recognize the singleton.
  // Appears that you must *both* bind it as a singleton *and* add the @Singleton annotation.
  @Singleton
  public static class TestMessenger implements X {

    private static AtomicInteger factoryCounter = new AtomicInteger(0);
    private final int instanceCounter;

    private String myMessage;

    @Inject
    public TestMessenger(@Named(KEY_MSG) String test) {
      this.instanceCounter = factoryCounter.incrementAndGet();
      this.myMessage = test;
      System.out.println(
          "Construct instance #" + this.instanceCounter + " with msg \"" + this.myMessage + "\"");
    }

    public String getMyMessage() {
      return myMessage;
    }

    @Override
    public void spitItOutAlready() {
      System.out.println(
          "SPIT IT OUT ALREADY! instance #" + this.instanceCounter + ": " + this.myMessage);
    }

    public int getInstanceCounter() {
      return instanceCounter;
    }

  }

  public static void main(String[] args) {

    final Injector createInjector = Guice.createInjector(new TestSingletonModule());

    for (int i = 0; i < 10; i++) {
      TestMessenger instance = createInjector.getInstance(TestMessenger.class);
      instance.spitItOutAlready();
    }

  }

}
