package com.roamgram.travelDiary;

import co.elastic.apm.attach.ElasticApmAttacher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class TravelDiaryApplication {

	public static void main(String[] args) {
		ElasticApmAttacher.attach();
		SpringApplication.run(TravelDiaryApplication.class, args);
	}

}
