package net.siudek.media.domain;

import java.io.File;
import java.util.Iterator;

public interface MediaSearch {

  public Iterator<MediaFile> find(File root);

}
