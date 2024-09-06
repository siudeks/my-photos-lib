package net.siudek.media.domain;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StateListeners {
  
  private final List<StateListener> listeners;

  void on(FileProcessingState event) {
    listeners.forEach(it -> it.on(event));
  }


}
