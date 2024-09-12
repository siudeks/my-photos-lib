package net.siudek.media.domain;

import java.io.File;
import java.util.Iterator;

import okio.Path;

/** Allows subscribe to all changes in filesystem - cinluding creation, modification, deletion and subfolders. */
public interface MediaSearch {

  /** One-shot review of existing media files. */
  public Iterator<MediaFile> find(File root);

}
