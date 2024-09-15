package net.siudek.media.domain;

/** List of all events related to single files, which can be generated from observed folder. */
public sealed interface ImageEvent {
  
  /** Media file found - already existing or just created. */
  record Found(Image path) implements ImageEvent {
  }

  /** New file has been just created */
  // record Created(Image value) implements ImageEvent { }

}
