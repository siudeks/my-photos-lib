package net.siudek.media.domain;

@FunctionalInterface
public interface CheckedSupplier<T> {

  T get() throws Exception;

}
