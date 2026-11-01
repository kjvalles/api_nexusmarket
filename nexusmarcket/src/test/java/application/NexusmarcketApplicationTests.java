package application;

import application.config.TestOutPortsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestOutPortsConfig.class)
class NexusmarcketApplicationTests {

	@Test
	void contextLoads() {
	}

}
