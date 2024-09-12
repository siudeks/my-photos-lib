package net.siudek.media.domain;

sealed interface Try<T> {

  static Try<Void> of(CheckedRunnable runnable) {
    return of(() -> { runnable.run(); return null; });
  }

  @SuppressWarnings("unchecked")
  static <T> Try<T> of(CheckedSupplier<T> supplier) {
    try {
      var value = supplier.get();
      return new Value<T>(value);
    } catch (Exception ex) {
      return new Error(ex);
    }
  }

  @SuppressWarnings("rawtypes")
  record Error(Exception ex) implements Try {
  }

  record Value<T>(T value) implements Try<T> {
  }  

}

