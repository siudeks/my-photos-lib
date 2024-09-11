package net.siudek.media.domain;

import java.util.function.Consumer;

public interface ImageDescService {

  void request(String jpgBase64, Consumer<String> responseHandler);

  void asEmbeddings(String text, Consumer<double[]> responseHandler);
  
}

