package net.siudek.media.domain;

import net.siudek.media.utils.CloseableQueue;

/** Allows to observe changes on folders and files. */
public interface FileEventQueue extends CloseableQueue<FileEvent> {
}
