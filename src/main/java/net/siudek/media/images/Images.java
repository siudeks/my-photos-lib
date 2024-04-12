package net.siudek.media.images;

import java.io.File;
import java.util.stream.Stream;

public class Images {

  Stream<Image> find(File root) {
    return Stream.empty();
  }

  sealed interface Image {

    record JPG(String name) implements Image {
    }

  }
}
