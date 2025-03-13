package edu.citadel.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring boot entry point for the API Application
 */
@ComponentScan(value = {"edu.citadel"})
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RestApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(RestApiApplication.class, args);
	}
}

