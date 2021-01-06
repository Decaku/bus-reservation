package com.stylefeng.guns.rest;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDubbo
//@EnableScheduling
public class GunsBusApplication {

    public static void main(String[] args) {
        SpringApplication.run(GunsBusApplication.class, args);
    }
}
