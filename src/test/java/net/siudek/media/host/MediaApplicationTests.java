package net.siudek.media.host;

import java.nio.file.Path;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		// interactive mode stops the test and waits for input
		// more: https://docs.spring.io/spring-shell/reference/execution.html
		"spring.shell.interactive.enabled=false"
})
class MediaApplicationTests {

	@Test
	void contextLoads() {
	}

  @Test
  void 
  happyPath() {

    var path = Path.of("./data");
    Assertions.assertThat(path.toFile()).exists();

    // 1. Locate files

    // 2. Translate files to json descriptions using llava

    // 3. Load json descriptions and translate them to embeddings using llama

    // 4. Load embeddings with file info (location and llava text) into database

    // 5. Check search by semantic
  }

}
