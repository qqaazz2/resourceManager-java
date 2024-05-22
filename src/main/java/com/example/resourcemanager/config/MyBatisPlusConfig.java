package com.example.resourcemanager.config;

import com.example.resourcemanager.common.MyMetaObjectHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.resourcemanager.mapper")
public class MyBatisPlusConfig {

    @Bean
    public MyMetaObjectHandler metaObjectHandler() {
        return new MyMetaObjectHandler();
    }
}
