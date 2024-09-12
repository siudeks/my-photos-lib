package net.siudek.media.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class TryTest {
  
  @Test
  void shouldSuccess1() {
    var actual = switch(Try.of(() -> 1)) {
      case Try.Value<Integer>(var value) -> value;
      case Try.Error(var ex) -> 0;
    };
    Assertions.assertThat(actual).isEqualTo(1);
  }

  @Test
  void shouldError1() {
    CheckedSupplier<Integer> s = () -> { throw new IllegalArgumentException(); };
    var actual = switch(Try.of(s)) {
      case Try.Value<Integer>(var value) -> value;
      case Try.Error(var ex) -> 2;
    };
    Assertions.assertThat(actual).isEqualTo(2);
  }

  @Test
  void shouldError2() {
    CheckedSupplier<Integer> s = () -> { throw new IllegalArgumentException(); };
    var actual = switch(Try.of(s)) {
      case Try.Value<Integer>(var value) -> value;
      case Try.Error(IllegalArgumentException ex) -> 3;
      case Try.Error(Exception ex) -> 2;
    };
    Assertions.assertThat(actual).isEqualTo(3);
  }

  @Test
  void shouldError3() {
    CheckedSupplier<Integer> s = () -> { throw new IllegalArgumentException(); };
    var actual = switch(Try.of(s)) {
      case Try.Value(var value) -> value;
      case Try.Error(NullPointerException ex) -> 3;
      case Try.Error(Exception ex) -> 2;
    };
    Assertions.assertThat(actual).isEqualTo(2);
  }

}
