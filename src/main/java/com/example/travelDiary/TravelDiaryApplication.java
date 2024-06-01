package com.example.travelDiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

//@SpringBootApplication(exclude = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
@SpringBootApplication
@Profile("test")
public class TravelDiaryApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelDiaryApplication.class, args);
	}

}
