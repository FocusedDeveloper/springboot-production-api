package com.focuseddeveloper.springboot_production_api;

import org.springframework.boot.SpringApplication;

public class TestSpringbootProductionApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringbootProductionApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
