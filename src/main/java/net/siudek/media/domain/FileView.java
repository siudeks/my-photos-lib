package net.siudek.media.domain;

import java.util.List;

import org.springframework.stereotype.Component;

/** */
@Component
public class FileView {

  /** Return list of images to satisfy you query. */
  List<Image> find(String query) {
    return List.of();
  }

}
