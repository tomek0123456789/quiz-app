package app.Quiz.jwzpQuizappProject;

import app.Quiz.jwzpQuizappProject.config.RsaKeyProperties;
import app.Quiz.jwzpQuizappProject.models.categories.CategoryModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.CategoryRepository;
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

	public JwzpQuizappProjectApplication(UserRepository userRepository, CategoryRepository categoryRepository, @Value("${app.debug}") boolean debug) {
		this.userRepository = userRepository;
		this.categoryRepository = categoryRepository;
		DEBUG = debug;
	}

	public static void main(String[] args) {
		SpringApplication.run(JwzpQuizappProjectApplication.class, args);
	}

	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;

	private final boolean DEBUG;

	@Bean
	public CommandLineRunner cli() {
		return args -> {
			if (DEBUG) {
				if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
					var user = new UserModel();
					userRepository.save(user);
					categoryRepository.save(new CategoryModel("Art"));
				}
			}
		};
	}
}
