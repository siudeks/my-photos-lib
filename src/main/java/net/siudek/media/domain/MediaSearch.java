package net.siudek.media.domain;

import java.nio.file.*;
import java.util.Iterator;

/** Allows subscribe to all changes in filesystem - cinluding creation, modification, deletion and subfolders. */
public interface MediaSearch {

  /** One-shot review of existing media files. */
  public Iterator<MediaFile> find(Path root);

}
