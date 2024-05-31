package com.example.resourcemanager.controller;

import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.common.UploadFile;
import com.example.resourcemanager.service.BooksDetailsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/booksDetails")
public class BooksDetailsController {
    @Resource
    UploadFile uploadFile;

    @Resource
    BooksDetailsService booksDetailsService;

    @GetMapping("/getDetailsList")
    public ResultResponse getDetailsList(@RequestParam Integer id) {
        return ResultResponse.success(booksDetailsService.getDetailsList(id));
    }

    @PostMapping("/upload")
    public ResultResponse upload(@RequestParam MultipartFile[] files, @RequestParam String name,@RequestParam Integer id) {
        String[] formats = {"epub"};
        String pathName = "/books/" + name;
        List<File> list = uploadFile.upload(files, pathName, formats);

        boolean isTrue = booksDetailsService.addDetails(id,list);
        if (!isTrue) new BizException("4000", "书籍创建失败");
        return ResultResponse.success();
    }
}
