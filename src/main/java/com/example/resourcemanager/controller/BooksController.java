package com.example.resourcemanager.controller;

import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.common.UploadFile;
import com.example.resourcemanager.entity.Books;
import com.example.resourcemanager.service.BooksService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping("/books")
public class BooksController {
    @Resource
    BooksService booksService;

    @Resource
    UploadFile uploadFile;

    @GetMapping("/getList")
    public ResultResponse getList(@RequestParam int page,@RequestParam int size) {
        Map<String,Object> map = booksService.getBooksList(page,size);
        return ResultResponse.success(map);
    }

    @PostMapping("/addBooks")
    public ResultResponse addBooks(@RequestBody Books books) {
        Integer id = booksService.addBooks(books);
        if (null == id) new BizException("4000", "新增失败");
        return ResultResponse.success();
    }

    @GetMapping("/uploadCover")
    public ResultResponse uploadCover(@RequestParam MultipartFile[] files) {
        String[] formats = {"jpg", "png"};
        List<File> list = uploadFile.upload(files, "booksCover", formats);
        return ResultResponse.success();
    }

    @PostMapping("/upload")
    public ResultResponse upload(@RequestParam MultipartFile[] files,@RequestParam String name) {
        String[] formats = {"epub"};
        String pathName = "/books/" + name;
        List<File> list = uploadFile.upload(files, pathName, formats);
        return ResultResponse.success();
    }

    @PostMapping("/editBooks")
    public ResultResponse editBooks(@RequestBody Books books) {
        System.out.println(books);
        Integer id = booksService.editBooks(books);
        if (null == id) new BizException("4000", "修改失败");
        return ResultResponse.success();
    }
}
