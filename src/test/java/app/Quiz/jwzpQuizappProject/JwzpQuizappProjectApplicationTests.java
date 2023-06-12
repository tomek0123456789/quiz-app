package app.Quiz.jwzpQuizappProject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

//@RunWith(SpringRunner.class)
@SpringBootTest(classes = JwzpQuizappProjectApplicationTests.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class JwzpQuizappProjectApplicationTests {

	@Test
	void contextLoads() {
		Assert.isTrue(true, "test assertion");
	}

}
