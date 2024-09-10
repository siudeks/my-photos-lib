package net.siudek.media.domain;

import java.nio.file.Path;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;

/** When app started, handles run arg and converts it to {@see AppEvent} */
@Component
@Profile("!test")
@RequiredArgsConstructor
class ArgsHandler implements ApplicationRunner {

  private final ApplicationEventPublisher publisher;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    var rootDirName = args.getOptionValues("dir").get(0);
    var asPath = Path.of(rootDirName);
    Assert.isTrue(asPath.toFile().exists(), "Root path exists");
    publisher.publishEvent(new AppEvent.RunArgs(asPath));
  }
}
