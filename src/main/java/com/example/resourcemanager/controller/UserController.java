package com.example.resourcemanager.controller;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.common.UploadFile;
import com.example.resourcemanager.dto.UserInfo;
import com.example.resourcemanager.entity.User;
import com.example.resourcemanager.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;

    @Resource
    UploadFile uploadFile;

    @Resource
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResultResponse login(@RequestBody User user) {
        userService.verifyCode(user.getKey(), user.getCode());
        Map<String, Object> map = userService.login(user);
        return ResultResponse.success(map);
    }

    @GetMapping("/code")
    public ResultResponse getCode() {
        return ResultResponse.success(userService.getCode());
    }

    @GetMapping("/info")
    public ResultResponse info() {
        return ResultResponse.success(userService.getUserInfo());
    }

    @PostMapping("/setInfo")
    public ResultResponse setUserInfo(@RequestBody UserInfo userInfo) {
        System.out.println(userInfo.getName());
        return ResultResponse.success(userService.setUserInfo(userInfo));
    }

    @GetMapping("/changeMystery")
    public ResultResponse changeMystery(Integer mystery, String mysteryPassword) {
        userService.changeMystery(mystery, mysteryPassword);
        return ResultResponse.success();
    }

    @PostMapping("/updatePassWord")
    public ResultResponse updatePassWord(@RequestBody UserInfo userInfo) {
        userService.updatePassWord(userInfo.getOldPassWord(), userInfo.getNewPassWord());
        return ResultResponse.success();
    }

    @PostMapping("/uploadCover")
    public ResultResponse uploadCover(@RequestParam MultipartFile[] files) {
        String[] formats = {"jpg", "png"};
        String pathName = "/user";
        List<File> list = uploadFile.upload(files, pathName, formats);
        userService.updateImage(list.get(0).getPath());
        return ResultResponse.success(list.get(0).getPath());
    }

    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication,null,1)")
    @PostMapping("/updateMysteryPassWord")
    public ResultResponse updateMysteryPassWord(@RequestBody UserInfo userInfo) {
        userService.updateMysteryPassWord(userInfo.getOldPassWord(), userInfo.getNewPassWord());
        return ResultResponse.success();
    }
}
