package com.example.resourcemanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class ResourceManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResourceManagerApplication.class, args);
    }
}