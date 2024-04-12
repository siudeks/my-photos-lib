package net.siudek.media.images;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.LinkedList;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

public class Images {

  @SneakyThrows
  Iterator<Image> find(File root) {
    var items = new LinkedList<Path>();
    var visitor = new Visitor(items);
    Files.walkFileTree(root.toPath(), visitor);
    return items.stream().map(it -> (Image) new Image.JPG(it.toFile().getName())).iterator();
  }

  sealed interface Image {

    record JPG(String name) implements Image {
    }

  }

  @RequiredArgsConstructor
  class Visitor implements FileVisitor<Path> {

    private final LinkedList<Path> queue;

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
