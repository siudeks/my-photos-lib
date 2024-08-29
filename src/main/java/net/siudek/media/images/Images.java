package net.siudek.media.images;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Finds and observes lifecycle of all images in requested folder(s). For each image identifies its state and applies next steps of image recognition.
 */
public class Images {

  @SneakyThrows
  Iterator<Image> find(File root) {
    var items = new LinkedBlockingQueue<Path>();
    var visitor = new Visitor(items);
    run(root, visitor);
    return items.stream().map(this::asImage).iterator();
  }

  @SneakyThrows
  Image asImage(Path image) {
    var filename = asFileName(image);
    var name = filename.name();
    var ext = filename.ext();
    var extension = Files.probeContentType(image);
    return switch (ext) {
      case "jpg" -> new Image.JPG(name);
      case "png" -> new Image.PNG(name);
      case "heic" -> new Image.HEIC(name);
      default -> throw new IllegalArgumentException("extension [" + extension + "] is not supported.");
    };
  }

  static Filename asFileName(Path image) {
    var asFile = image.toFile();
    var imageName = asFile.getName();
    var name = FilenameUtils.getBaseName(imageName);
    var ext = FilenameUtils.getExtension(imageName);
    return new Filename(name, ext);
  }

  record Filename(String name, String ext) {
  }

  sealed interface Image {
    record JPG(String fileName) implements Image { }
    record PNG(String fileName) implements Image { }
    record HEIC(String fileName) implements Image { }
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
}
