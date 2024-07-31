package com.example.resourcemanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ResourceManageApplicationRunner implements ApplicationRunner {
    @Value("${file.upload}")
    String filePath;

    String comic = "comic";
    String picture = "picture";
    String books = "books";
    String[] array = new String[]{comic, picture, books};

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (String path : array) {
            File file = new File(filePath + path);
            if (!file.exists()) file.mkdirs();
        }
    }
}
