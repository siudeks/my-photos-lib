package net.siudek.media.domain;

public interface StateListener {
  
  record State(int discovered, int indexed) { }

  void on(FileProcessingState event);
  
}
