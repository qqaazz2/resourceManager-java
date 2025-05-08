package com.example.resourcemanager.controller.adult;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.common.UploadFile;
import com.example.resourcemanager.dto.adult.AdultAuthorDTO;
import com.example.resourcemanager.service.adult.AdultAuthorService;
import com.example.resourcemanager.service.adult.AdultTagsService;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/adultTags")
public class AdultTagsController {
    @Resource
    AdultTagsService adultTagsService;

    @GetMapping("/delData")
    public ResultResponse delData(@Validated @RequestParam Integer id){
        adultTagsService.delData(id);
        return ResultResponse.success();
    }
}
