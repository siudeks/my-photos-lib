package net.siudek.media.images;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
    return items.stream().map(it -> (Image) new Image.JPG(it.toFile().getName())).iterator();
  }

  sealed interface Image {

    record JPG(String name) implements Image { }
    record PNG(String name) implements Image { }
    record HEIC(String name) implements Image { }

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
