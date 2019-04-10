package com.kemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.kemo")
public class ClusterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClusterApplication.class, args);
    }
}
