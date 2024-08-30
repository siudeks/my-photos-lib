package net.siudek.media.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.cglib.core.internal.Function;

import lombok.experimental.UtilityClass;
import java.nio.file.Path;

@UtilityClass
public class FileUtils {

  public static <T> T asFilename(Path image, Function<Filename, T> extractor) {
    var asFilename = asFilename(image);
    return extractor.apply(asFilename);
  }

  public static Filename asFilename(Path image) {
    var asFile = image.toFile();
    var imageName = asFile.getName();
    var name = FilenameUtils.getBaseName(imageName);
    var ext = FilenameUtils.getExtension(imageName);
    return new Filename(name, ext);
  }
  
  public record Filename(String name, String ext) {
  }
}
