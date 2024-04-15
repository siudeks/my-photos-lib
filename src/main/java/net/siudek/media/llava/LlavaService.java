package net.siudek.media.llava;

import java.io.File;

import org.springframework.stereotype.Component;

public interface LlavaService {

  String describe(File jpgImage);
}

@Component
class LlavaServiceImpl implements LlavaService {

  @Override
  public String describe(File jpgImage) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'describe'");
  }

}
