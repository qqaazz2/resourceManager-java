package com.example.resourcemanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;

public interface UserService extends IService<User> {
    Map<String,String> login(User user);

    Map<String,String> getCode();

    void verifyCode(String key,String code);
}
