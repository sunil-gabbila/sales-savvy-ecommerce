package com.kodnest.salessavvy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;


@SpringBootApplication
@ServletComponentScan 
public class SalesSavvyEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalesSavvyEcommerceApplication.class, args);
    }
}