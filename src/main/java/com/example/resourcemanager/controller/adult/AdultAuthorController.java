package com.example.resourcemanager.controller.adult;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.dto.adult.AdultAuthorDTO;
import com.example.resourcemanager.dto.adult.AdultDetailDTO;
import com.example.resourcemanager.dto.adult.AdultListQueryCondition;
import com.example.resourcemanager.service.adult.AdultAuthorService;
import com.example.resourcemanager.service.adult.AdultService;
import com.example.resourcemanager.service.adult.AdultTagsService;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import com.example.resourcemanager.common.UploadFile;

@RestController
@RequestMapping("/adultAuthor")
public class AdultAuthorController {
    @Resource
    AdultAuthorService adultAuthorService;

    @Resource
    UploadFile uploadFile;

    @PostMapping("/upload")
    public ResultResponse upload(@RequestParam MultipartFile[] files) {
        String[] formats = {"jpg", "png"};
        String pathName = "/adult/author/";
        List<File> list = uploadFile.upload(files, pathName, formats);
        return ResultResponse.success(list.get(0).getPath());
    }

    @PostMapping("/addData")
    public ResultResponse addData(@Validated(Insert.class) @RequestBody AdultAuthorDTO author){
        adultAuthorService.addData(author);
        return ResultResponse.success();
    }

    @PostMapping("/editData")
    public ResultResponse editData(@Validated(Update.class) @RequestBody AdultAuthorDTO author){
        adultAuthorService.editData(author);
        return ResultResponse.success();
    }

    @GetMapping("/delData")
    public ResultResponse delData(@Validated @RequestParam Integer id){
        adultAuthorService.delData(id);
        return ResultResponse.success();
    }
}
