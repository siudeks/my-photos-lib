package net.siudek.media.domain;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/** Shared, in-memory state of the processing */
@Component
public class State {

  private final ConcurrentHashMap<String, StateValue> watchedFiles = new ConcurrentHashMap<>();

  interface StateValue {
  }
}
