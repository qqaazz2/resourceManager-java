package com.example.resourcemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.entity.LoginUser;
import com.example.resourcemanager.entity.User;
import com.example.resourcemanager.mapper.UserMapper;
import com.example.resourcemanager.service.TokenService;
import com.example.resourcemanager.service.UserService;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.SpecCaptcha;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    TokenService tokenService;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    AuthenticationManager authenticationManager;

    private static final String codeKey = "CodeKey:";

    @Override
    public Map<String, String> login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String token = tokenService.createToken(loginUser.getUser());
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return map;
    }

    @Override
    public Map<String, String> getCode() {
        SpecCaptcha  captcha = new SpecCaptcha(111,36);
        captcha.setLen(5);
        String code = captcha.text();
        UUID uuid = UUID.randomUUID();

        redisTemplate.opsForValue().set(codeKey + uuid, code);
        redisTemplate.expire(codeKey + uuid, 60, TimeUnit.SECONDS);
        Map<String, String> map = new HashMap<>();
        map.put("key", uuid.toString());
        map.put("img", captcha.toBase64());
        return map;
    }

    @Override
    public void verifyCode(String key, String code) {
        String redisCode = (String) redisTemplate.opsForValue().get(codeKey + key);
        if (redisCode == null) {
            throw new BizException("4000", "验证码过期了");
        } else if (!redisCode.equalsIgnoreCase(code)) {
            throw new BizException("4000", "验证码错误");
        }

        redisTemplate.delete(codeKey + key);
    }
}
