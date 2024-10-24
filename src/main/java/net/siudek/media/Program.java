package net.siudek.media;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Program {

  @org.jspecify.annotations.NonNull String aaa = null;

	public static void main(String[] args) {
		SpringApplication.run(Program.class, args);
	}

}

@NullMarked
class Example {
  void useNullable(@Nullable String x) {}
  void useNonNull(@NonNull String x) {}

  void example(@Nullable String nullable, @NonNull String nonNull) {
    useNullable(nonNull); // JSpecify allows this
    useNonNull(nullable); // JSpecify doesn't allow this
  }
}
