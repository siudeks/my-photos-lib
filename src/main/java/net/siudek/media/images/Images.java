package net.siudek.media.images;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Base64;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;

import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.siudek.media.utils.FileUtils;

/**
 * Finds and observes lifecycle of all images in requested folder(s). For each image identifies its state and applies next steps of image recognition.
 */
public class Images {

  @SneakyThrows
  public Iterator<Image> find(File root) {
    var items = new LinkedBlockingQueue<Path>();
    var visitor = new Visitor(items);
    run(root, visitor);
    return items.stream().map(Images::asImage).iterator();
  }

  @SneakyThrows
  public static Image asImage(Path image) {
    var ext = FileUtils.asFilename(image, it -> it.ext());
    var extension = Files.probeContentType(image);
    return switch (ext) {
      case "jpg" -> new Image.JPG(image);
      case "png" -> new Image.PNG(image);
      case "heic" -> new Image.HEIC(image);
      default -> throw new IllegalArgumentException("extension [" + extension + "] is not supported.");
    };
  }

  sealed interface Image {

    Path path();

    record JPG(Path path) implements Image { }
    record PNG(Path path) implements Image { }
    record HEIC(Path path) implements Image { }
  }

  @SneakyThrows
  void run(File root, Visitor visitor) {
    Files.walkFileTree(root.toPath(), visitor);
  }

  @RequiredArgsConstructor
  final class Visitor implements FileVisitor<Path> {

    private final BlockingQueue<Path> queue;

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      queue.add(file);
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
      return FileVisitResult.CONTINUE;
    }

  }

  /* Converts (in memory) given file to JPG representation, and then to Base64. */
  @SneakyThrows
  public static String asJpegBase64(Image image) {
    var asBytes = Files.readAllBytes(image.path());
    var asStream = new ByteArrayInputStream(asBytes);

    var bufferedImage = ImageIO.read(asStream);
    Assert.notNull(bufferedImage, () -> "Image " + image + " can't be converted to BufferedImage");

    var formatName = switch(image) {
      case Image.JPG it -> "jpeg";
      case Image.PNG it -> "png";
      case Image.HEIC it -> "heic";
    };

    var binaryOut = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, formatName, binaryOut);

    return Base64.getEncoder().encodeToString(binaryOut.toByteArray());
  }
}
