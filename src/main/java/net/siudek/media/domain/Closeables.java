package net.siudek.media.domain;

import java.util.Arrays;

public class Closeables {

  private Closeables() {
    // prevents creation of utility class
  }

  public static CompositeCloseable of(AutoCloseable... items) {
    var result = new CompositeCloseable();
    Arrays.stream(items).forEach(result::add);
    return result;
  }
}
