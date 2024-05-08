package com.example.resourcemanager.controller;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.entity.User;
import com.example.resourcemanager.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Resource
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResultResponse login(@RequestBody User user) {
        userService.verifyCode(user.getKey(), user.getCode());
        Map<String, String> map = userService.login(user);
        return ResultResponse.success(map);
    }

    @GetMapping("/code")
    public ResultResponse getCode() {
        return ResultResponse.success(userService.getCode());
    }

    @GetMapping("/info")
    public void info() {
        System.out.println(123);
    }
}
