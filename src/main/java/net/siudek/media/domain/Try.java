package net.siudek.media.domain;

sealed interface Try<T> permits Try.TryVoid, Try.TryValue {

  static TryVoid of(CheckedRunnable runnable) {
    try {
      runnable.run();
      return Success.INSTANCE;
    } catch (Exception ex) {
      return new Error(ex);
    }
  }

  @SuppressWarnings("unchecked")
  static <T> TryValue<T> of(CheckedSupplier<T> supplier) {
    try {
      var value = supplier.get();
      return new Value<T>(value);
    } catch (Exception ex) {
      return new Error(ex);
    }
  }

  sealed interface TryVoid extends Try permits Success, Error {
  }

  sealed interface TryValue<T> extends Try<T> permits Value, Error {
  }

  record Error(Exception ex) implements TryVoid, TryValue {
  }

  record Value<T>(T value) implements TryValue<T> {
  }


  enum Success implements TryVoid {
    INSTANCE
  }

  @FunctionalInterface
  public interface CheckedRunnable {
    void run() throws Exception;
  }

  @FunctionalInterface
  public interface CheckedSupplier<T> {
    T get() throws Exception;
  }

}

