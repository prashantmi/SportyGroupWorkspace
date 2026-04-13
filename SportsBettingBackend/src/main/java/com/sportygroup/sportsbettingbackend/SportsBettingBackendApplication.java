package com.sportygroup.sportsbettingbackend;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SportsBettingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportsBettingBackendApplication.class, args);
	}

}
