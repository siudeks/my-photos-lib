package net.siudek.media.domain;

import net.siudek.media.utils.CloseableQueue;

public interface FileEventQueue extends CloseableQueue<FileEvent> {
  
}
