package com.unity3d.project.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.unity3d.project" })
public class SdetProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdetProjectApplication.class, args);
	}
}
