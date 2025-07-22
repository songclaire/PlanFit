package com.project.PlanFit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PlanFitApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanFitApplication.class, args);
	}

}
