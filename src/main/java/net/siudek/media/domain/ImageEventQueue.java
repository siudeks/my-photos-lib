package net.siudek.media.domain;

import net.siudek.media.utils.CloseableQueue;

/** Allows to observe changes on media files. */
public interface ImageEventQueue extends CloseableQueue<ImageEvent> {
}
