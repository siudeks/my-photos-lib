package net.siudek.media.domain;

@FunctionalInterface
public interface CheckedRunnable {

  void run() throws Exception;

}
