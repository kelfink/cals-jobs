package gov.ca.cwds.jobs.util.jdbc;

import java.util.function.Function;
import java.util.stream.Stream;

public class NeutronStreamUtils {

  public static <T> Function<T, Stream<T>> everyNth(int n) {
    return new Function<T, Stream<T>>() {
      int i = 0;
  
      @Override
      public Stream<T> apply(T t) {
        if (i++ % n == 0) {
          return Stream.of(t);
        }
        return Stream.empty();
      }
  
    };
  }

}
