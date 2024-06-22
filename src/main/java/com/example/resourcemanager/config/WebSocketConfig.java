package com.example.resourcemanager.config;

import com.example.resourcemanager.channel.BooksChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter (){
        ServerEndpointExporter exporter = new ServerEndpointExporter();
        return exporter;
    }
}