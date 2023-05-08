package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.config.RsaKeyProperties;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class JwzpQuizappProjectApplication {

	public JwzpQuizappProjectApplication(UserRepository userRepository, @Value("${app.debug}") boolean debug) {
		this.userRepository = userRepository;
		DEBUG = debug;
	}

	public static void main(String[] args) {
		SpringApplication.run(JwzpQuizappProjectApplication.class, args);
	}

	private final UserRepository userRepository;

	private final boolean DEBUG;

	@Bean
	public CommandLineRunner cli() {
		return args -> {
			if (DEBUG) {
				var user = new UserModel();
				System.out.println(user.getPassword());
				userRepository.save(user);
			}
		};
	}
}
