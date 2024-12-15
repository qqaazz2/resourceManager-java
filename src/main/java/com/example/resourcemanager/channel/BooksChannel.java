package com.example.resourcemanager.channel;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.resourcemanager.entity.books.Books;
import com.example.resourcemanager.entity.books.BooksDetails;
import com.example.resourcemanager.mapper.BooksDetailsMapper;
import com.example.resourcemanager.mapper.BooksMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
@ServerEndpoint(value = "/webSocket/books/read/{detailsID}")
public class BooksChannel implements ApplicationContextAware {
    private static RedisTemplate redisTemplate;

    private static BooksDetailsMapper booksDetailsMapper;

    private Session session;
    private static final String keyPrefix = "BooksDetails";
    private Integer detailsID;
    private BooksDetails booksDetails;
    private static ApplicationContext applicationContext;
    private BooksMapper booksMapper;
    private Date date;


    @OnOpen
    public void onOpen(Session session, @PathParam("detailsID") Integer detailsID) {
        this.session = session;
        this.detailsID = detailsID;
        booksDetailsMapper = applicationContext.getBean(BooksDetailsMapper.class);
        redisTemplate = applicationContext.getBean("redisTemplate", RedisTemplate.class);
        booksMapper = applicationContext.getBean(BooksMapper.class);
        this.booksDetails = booksDetailsMapper.selectById(detailsID);
        date = new Date();
    }

    @OnClose
    public void onClose(Session session) {
        if (!redisTemplate.hasKey(keyPrefix + detailsID)) return;
        Float progress = (Float) redisTemplate.opsForValue().get(keyPrefix + detailsID);
        if (booksDetails.getProgress() == progress) return;
        if (progress >= 1) {
            progress = Float.valueOf(1);
            booksDetails.setStatus(2);
        } else if (progress > 0) {
            booksDetails.setStatus(3);
        }
        booksDetails.setRead_time(date);
        booksDetails.setProgress(progress);
        booksDetailsMapper.updateById(booksDetails);
        booksMapper.update(new UpdateWrapper<Books>().set("last_read", new Date()));
    }

    @OnMessage
    public void onMessage(Float message, Session session) {
        redisTemplate.opsForValue().set(keyPrefix + detailsID, message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        this.session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
        System.out.println(throwable.getMessage());
        throwable.printStackTrace();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }
}