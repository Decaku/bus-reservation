package com.stylefeng.guns.rest;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class GunsOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(GunsOrderApplication.class, args);
	}

}
