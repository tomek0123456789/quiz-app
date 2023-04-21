package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class JwzpQuizappProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwzpQuizappProjectApplication.class, args);
	}

}
