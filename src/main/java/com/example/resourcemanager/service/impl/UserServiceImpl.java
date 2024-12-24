package com.example.resourcemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.dto.UserInfo;
import com.example.resourcemanager.entity.LoginUser;
import com.example.resourcemanager.entity.User;
import com.example.resourcemanager.enums.ExceptionEnum;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Resource
    PasswordEncoder passwordEncoder;

    private static final String codeKey = "CodeKey:";

    @Override
    public Map<String, Object> login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String token = tokenService.createToken(loginUser.getUser());

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(loginUser.getUser().getEmail());
        userInfo.setName(loginUser.getUser().getName());
        userInfo.setMystery(loginUser.getUser().getMystery());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("userInfo", userInfo);
        return map;
    }

    @Override
    public Map<String, String> getCode() {
        SpecCaptcha captcha = new SpecCaptcha(111, 36);
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

    @Override
    public UserInfo getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            UserInfo userInfo = new UserInfo();
            userInfo.setEmail(loginUser.getUser().getEmail());
            userInfo.setName(loginUser.getUser().getName());
            userInfo.setMystery(loginUser.getUser().getMystery());
            return userInfo;
        }
        throw new BizException(ExceptionEnum.SIGNATURE_NOT_MATCH);
    }

    @Override
    public UserInfo setUserInfo(UserInfo userInfo) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            updateWrapper.eq(User::getId, loginUser.getUser().getId());
            System.out.println(userInfo.getName());
            updateWrapper.set(userInfo.getName() != null, User::getName, userInfo.getName());
            updateWrapper.set(userInfo.getEmail() != null, User::getEmail, userInfo.getEmail());
            boolean isTrue = this.update(updateWrapper);
            if (!isTrue) throw new BizException("4000", "修改用户数据失败");

            loginUser.getUser().setEmail(userInfo.getEmail());
            loginUser.getUser().setName(userInfo.getName());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, authentication.getCredentials());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            return userInfo;
        }

        throw new BizException(ExceptionEnum.INTERNAL_SERVER_ERROR);
    }

    @Override
    public void changeMystery(Integer mystery, String mysteryPassword) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            if (mystery == 1 && !(passwordEncoder.matches(mysteryPassword, loginUser.getUser().getMysteryPassword()))) {
                throw new BizException("4000", "密码错误，请重试");
            }

            updateWrapper.eq(User::getId, loginUser.getUser().getId());
            updateWrapper.set(User::getMystery, mystery);
            boolean isTrue = this.update(updateWrapper);
            if (!isTrue) throw new BizException("4000", "神秘开关状态修改失败");

            loginUser.getUser().setMystery(mystery);
            System.out.println(loginUser.getUser().getMystery());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, authentication.getCredentials());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            throw new BizException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updatePassWord(String oldPassWord, String newPassword) {
        System.out.println(oldPassWord);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            if(!passwordEncoder.matches(oldPassWord,loginUser.getUser().getPassword())){
                throw new BizException("4000", "旧密码错误，修改密码失败");
            }

            updateWrapper.eq(User::getId, loginUser.getUser().getId());
            updateWrapper.set(User::getPassword, passwordEncoder.encode(newPassword));
            boolean isTrue = this.update(updateWrapper);
            if (!isTrue) throw new BizException("4000", "密码修改失败");
        } else {
            throw new BizException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateMysteryPassWord(String oldPassWord, String newPassword) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            if(!passwordEncoder.matches(oldPassWord,loginUser.getUser().getMysteryPassword())){
                throw new BizException("4000", "旧密码错误，修改密码失败");
            }

            String mysteryPassWord = passwordEncoder.encode(newPassword);
            updateWrapper.eq(User::getId, loginUser.getUser().getId());
            updateWrapper.set(User::getMysteryPassword, mysteryPassWord);
            boolean isTrue = this.update(updateWrapper);
            if (!isTrue) throw new BizException("4000", "神秘开关密码修改失败");

            loginUser.getUser().setMysteryPassword(mysteryPassWord);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, authentication.getCredentials());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            throw new BizException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
