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

import org.springframework.stereotype.Component;

import net.siudek.media.domain.ImageUtils;
import net.siudek.media.domain.MediaFile;
import net.siudek.media.domain.MediaSearch;

/**
 * Finds and observes lifecycle of all images in requested folder(s). For each image identifies its state and applies next steps of image recognition.
 */
@Component
class Images implements MediaSearch {

  @Override
  public Iterator<MediaFile> find(Path root) {
    var items = new LinkedBlockingQueue<Path>();
    var visitor = new Visitor(items);
    run(root.toFile(), visitor);
    return items.stream().map(ImageUtils::asMediaFile).iterator();
  }

  void run(File root, Visitor visitor) {
    try {
      Files.walkFileTree(root.toPath(), visitor);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  final class Visitor implements FileVisitor<Path> {

    private final BlockingQueue<Path> queue;

    public Visitor(BlockingQueue<Path> queue) {
      this.queue = queue;
    }

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
