package net.siudek.media.domain;

import java.util.function.Consumer;

public interface ImageDescService {

  void request(String jpgBase64, Consumer<String> responseHandler);
  
}

