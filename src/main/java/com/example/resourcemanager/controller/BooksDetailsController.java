package com.example.resourcemanager.controller;

import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.common.UploadFile;
import com.example.resourcemanager.entity.books.BooksDetails;
import com.example.resourcemanager.service.BooksDetailsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/booksDetails")
public class BooksDetailsController {
    @Resource
    UploadFile uploadFile;

    @Resource
    BooksDetailsService booksDetailsService;

    @GetMapping("/getDetailsList")
    public ResultResponse getDetailsList(@RequestParam Integer id,@RequestParam Integer page,@RequestParam Integer size) {
        return ResultResponse.success(booksDetailsService.getDetailsList(id,page,size));
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

    @GetMapping ("/delete")
    public ResultResponse delete(@RequestParam List<Integer> ids,@RequestParam Integer bookID,@RequestParam boolean delFile){
        Boolean isTrue = booksDetailsService.deleteDetails(ids, bookID,delFile);
        if(!isTrue) new BizException("4000","删除书籍失败");
        return ResultResponse.success();
    }

    @PostMapping("/edit")
    public ResultResponse edit(@RequestBody BooksDetails booksDetails){
        booksDetailsService.editDetails(booksDetails);
        return ResultResponse.success();
    }

    @GetMapping("/changeProgress")
    public ResultResponse changeProgress(@RequestParam Integer id,@RequestParam Float progress){
        booksDetailsService.changeProgress(id,progress);
        return ResultResponse.success();
    }
}
