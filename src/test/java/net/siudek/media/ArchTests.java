package net.siudek.media;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ArchTests {

    ApplicationModules modules = ApplicationModules.of(Program.class);

	@Test
	void verifyModularity() {

    Assertions.assertThat(modules).size().isNotZero();
    
    // Writing the application module arranagement to the console
    modules.forEach(System.out::println);
    
    // Trigger verification
    modules.verify();
	}

  // @Test
  // void writeDocumentationSnippets() {

  //   new Documenter(modules)
  //     .writeModulesAsPlantUml()
  //     .writeIndividualModulesAsPlantUml();
  // }
}

