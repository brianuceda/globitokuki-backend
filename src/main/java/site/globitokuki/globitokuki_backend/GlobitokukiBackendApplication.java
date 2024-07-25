package site.globitokuki.globitokuki_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import site.globitokuki.globitokuki_backend.repositories.UserRepository;

@SpringBootApplication
public class GlobitokukiBackendApplication {
  public static void main(String[] args) {
    SpringApplication.run(GlobitokukiBackendApplication.class, args);
  }

  @Bean
  public CommandLineRunner initData(
	  UserRepository userRepository
  ) {
    return args -> {
      // Add some initial data
	  };
  }
}
