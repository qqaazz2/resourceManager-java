package com.example.resourcemanager.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class FilePathConfig implements WebMvcConfigurer {
    @Value("${file.upload}")
    String url;

    @SneakyThrows
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File file = new File(url);
        registry.addResourceHandler("/files/**").addResourceLocations("file:" + file.getAbsolutePath() + "/");
    }
}
