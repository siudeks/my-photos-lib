package net.siudek.media.domain;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class StateListeners {
  
  private final List<StateListener> listeners;

  public StateListeners(List<StateListener> listeners) {
    this.listeners = listeners;
  }

  void on(FileProcessingState event) {
    listeners.forEach(it -> it.on(event));
  }


}
