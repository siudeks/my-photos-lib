package net.siudek.media.utils;

public interface SafeCloseable extends AutoCloseable {
  
  void close();
}
