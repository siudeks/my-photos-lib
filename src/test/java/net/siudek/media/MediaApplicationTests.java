package net.siudek.media;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		// interactive mode stops the test and waits for input
		"spring.shell.interactive.enabled=false"
})
class MediaApplicationTests {

	@Test
	void contextLoads() {
	}

}
