package net.siudek.media.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.util.Assert;

import net.siudek.media.utils.FileUtils;

public class ImageUtils {

  public static MediaFile asMediaFile(Path image) {
    var ext = FileUtils.asFilename(image, it -> it.ext());
    return switch (ext) {
      case "jpg" -> new Image.JPG(image);
      case "png" -> new Image.PNG(image);
      case "heic" -> new Image.HEIC(image);
      case "sha256" -> new MediaFile.Sha256(image);
      case "desc" -> new MediaFile.Desc(image);
      case "ignore" -> new MediaFile.Ignore(image);
      default -> throw new IllegalArgumentException("File extension [" + ext + "] is not supported.");
    };
  }

  /* Converts (in memory) given file to JPG representation, and then to Base64. */
  public static String asJpegBase64(Image image) throws Exception {
    return switch(image) {
      case Image.HEIC it -> asJpegBase64(it);
      case Image.JPG it -> asJpegBase64(it);
      case Image.PNG it -> asJpegBase64(it);
    };
  }

  public static String asJpegBase64(Image.HEIC image) throws Exception {
    var tempFile = Files.createTempFile("heic-", ".jpg");
    try (AutoCloseable delTempFile = () -> Files.delete(tempFile)) {
    var process = new ProcessBuilder()
      .command("heif-convert", image.path().toFile().getAbsolutePath(), tempFile.toFile().getAbsolutePath())
      .start();
    process.waitFor();
    var jpgBytes = Files.readAllBytes(tempFile);
    return Base64.getEncoder().encodeToString(jpgBytes);
    }
  }

  public static String asJpegBase64(Image.JPG image) throws IOException {
    var jpgBytes = Files.readAllBytes(image.path());
    return Base64.getEncoder().encodeToString(jpgBytes);
  }

  public static String asJpegBase64(Image.PNG image) throws IOException {
    var asBytes = Files.readAllBytes(image.path());
    var asStream = new ByteArrayInputStream(asBytes);

    var bufferedImage = ImageIO.read(asStream);
    Assert.notNull(bufferedImage, () -> "Image " + image + " can't be converted to BufferedImage");

    var binaryOut = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, "jpeg", binaryOut);
    var jpgBytes = binaryOut.toByteArray();

    return Base64.getEncoder().encodeToString(jpgBytes);
  }

}
