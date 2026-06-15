package com.theara.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

@SpringBootApplication
public class ErpPosApiApplication {
    public static void main(String[] args) {
        System.out.println("==========Application is starting " + LocalDateTime.now() + "==========");
        SpringApplication.run(ErpPosApiApplication.class, args);
        System.out.println("==========Application started successfully" + LocalDateTime.now() + "==========");
    }
}
