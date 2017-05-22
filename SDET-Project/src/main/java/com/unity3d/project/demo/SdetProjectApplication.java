package com.unity3d.project.demo;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.builder.SpringApplicationBuilder; 
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = { "com.unity3d.project" }, exclude = org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class)
public class SdetProjectApplication extends SpringBootServletInitializer {

	 @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(SdetProjectApplication.class);
	    }

	    public static void main(String[] args) throws Exception {
	        SpringApplication.run(SdetProjectApplication.class, args);
	    }
	    
	    
}
