package net.siudek.media.domain;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;

/** Single container to keep lal domain-level JFR events. */
public interface Events {
  
  @Category("bbbbbbbbbbbbbbb")
  @Name(ImageFound.CLASS_NAME)
  @Label("aaaaaaaaaaaaa")
  class ImageFound extends Event {
    
    static final String CLASS_NAME = "a.a.aaaaa";

    static final String FIELD_NAME_FILE_PATH = "filePath";
    @Name(FIELD_NAME_FILE_PATH)
    public final String filePath;

    public ImageFound(String filePath) {
      this.filePath = filePath;
    }
  }
}
