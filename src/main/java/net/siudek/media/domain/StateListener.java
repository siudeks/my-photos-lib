package net.siudek.media.domain;

public interface StateListener {
  
  record State(int discovered) { }

  void on(FileProcessingState event);
  
}
