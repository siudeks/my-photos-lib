package net.siudek.media.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import net.siudek.media.utils.CloseableQueueImpl;

@Configuration
@ComponentScan
public class DomainConfigurer {
  
  @Bean
  FileEventQueue fileEvents() {
    return new FileEventQueueImpl();
  }

  public static class FileEventQueueImpl extends CloseableQueueImpl<FileEvent> implements FileEventQueue {
  };

}
