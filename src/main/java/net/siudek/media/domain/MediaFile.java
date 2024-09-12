package net.siudek.media.domain;

import java.nio.file.Path;

public sealed interface MediaFile permits Image,
                                          MediaFile.Sha256,
                                          MediaFile.Desc,
                                          MediaFile.Ignore {
  
    record Sha256(Path path) implements MediaFile { }
    record Desc(Path path) implements MediaFile { }
    record Ignore(Path path) implements MediaFile { }

}
