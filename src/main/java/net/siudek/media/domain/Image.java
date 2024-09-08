package net.siudek.media.domain;

import java.nio.file.Path;

public sealed interface Image extends MediaFile {

  Path path();

  record JPG(Path path) implements Image { }
  record PNG(Path path) implements Image { }
  record HEIC(Path path) implements Image { }
}

