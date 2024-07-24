package com.example.resourcemanager.controller.comic;

import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.common.UploadFile;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.service.comic.ComicService;
import com.example.resourcemanager.task.ComicTask;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comic")
public class ComicController {
    @Resource
    ComicService comicService;

    @Resource
    UploadFile uploadFile;

    @Resource
    ComicTask comicTask;

    @PostMapping("/upload")
    public ResultResponse upload(@RequestParam MultipartFile[] files, @RequestParam(required = false,defaultValue = "0") Integer id) {
        String[] formats = {"zip"};
        Map<String, Files> map = uploadFile.uploadFile(files," /comic/", formats,2);

        comicService.addComic(map,id);
        return ResultResponse.success();
    }

    @GetMapping("/scanning")
    public ResultResponse upload(@RequestParam(required = false,defaultValue = "") String path){
        comicTask.start(path);
        return ResultResponse.success();
    }
}
