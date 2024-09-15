package net.siudek.media.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import net.siudek.media.utils.CloseableQueueImpl;

@Configuration
@ComponentScan
public class DomainConfigurer {
  
  @Bean
  ImageEventQueue imageEventQueue() {
    return new ImageEventQueueImpl();
  }

  @Bean
  FileEventQueue fileEventQueue() {
    return new FileEventQueueImpl();
  }

  public static class ImageEventQueueImpl extends CloseableQueueImpl<ImageEvent> implements ImageEventQueue {
  };

  public static class FileEventQueueImpl extends CloseableQueueImpl<FileEvent> implements FileEventQueue {
  };

}
