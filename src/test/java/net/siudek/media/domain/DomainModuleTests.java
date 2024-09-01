package net.siudek.media.domain;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode;

import net.siudek.media.LocalTest;

@ApplicationModuleTest(mode = BootstrapMode.ALL_DEPENDENCIES)
@LocalTest
public class DomainModuleTests {
  
  @Test
  void contextInit() {
  }

}
