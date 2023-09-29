package main.controller;

import java.time.LocalDate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigClass {
	@Bean
	LocalDate updateDate() {
		return LocalDate.now().minusDays(1);
	}
}
