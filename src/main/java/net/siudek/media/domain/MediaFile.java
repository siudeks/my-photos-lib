package net.siudek.media.domain;

import java.nio.file.Path;

public sealed interface MediaFile permits Image, MediaFile.Sha256 {
  
    record Sha256(Path path) implements MediaFile { }

}
