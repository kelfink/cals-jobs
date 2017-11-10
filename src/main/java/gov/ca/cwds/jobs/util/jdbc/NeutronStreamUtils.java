package gov.ca.cwds.jobs.util.jdbc;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Stream;

public final class NeutronStreamUtils {

  private NeutronStreamUtils() {
    // static methods only
  }

  public static String getFilePath(String path) {
    return path.substring(0, path.lastIndexOf(File.separatorChar));
  }

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
