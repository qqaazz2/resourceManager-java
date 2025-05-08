package com.example.resourcemanager.controller.adult;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.common.UploadFile;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.adult.AdultDetailDTO;
import com.example.resourcemanager.dto.adult.AdultListDTO;
import com.example.resourcemanager.dto.adult.AdultListQueryCondition;
import com.example.resourcemanager.dto.comic.ComicSetDetailDTO;
import com.example.resourcemanager.entity.adult.Adult;
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

@RestController
@RequestMapping("/adult")
public class AdultController {
    @Resource
    AdultService adultService;

    @Resource
    AdultTagsService adultTagsService;

    @Resource
    AdultAuthorService adultAuthorService;

    @Resource
    UploadFile uploadFile;

    @GetMapping("/getList")
    public ResultResponse getList(AdultListQueryCondition condition) {
        return ResultResponse.success(adultService.getList(condition));
    }

    @GetMapping("/getImages")
    public ResultResponse getImages(@RequestParam String number) {
        return ResultResponse.success(adultService.getImages(number));
    }

    @GetMapping("/randData")
    public ResultResponse randData() {
        return ResultResponse.success(adultService.randData());
    }

    @GetMapping("/getDetail")
    public ResultResponse getDetail(@RequestParam Integer id) {
        return ResultResponse.success(adultService.getOne(id));
    }

    @PostMapping("/addData")
    public ResultResponse addData(@Validated({Insert.class}) @RequestBody AdultDetailDTO adult) {
        return ResultResponse.success(adultService.addData(adult));
    }

    @PostMapping("/editData")
    public ResultResponse editData(@Validated({Update.class}) @RequestBody AdultDetailDTO adult) {
        return ResultResponse.success(adultService.editData(adult));
    }

    @GetMapping("/getTagList")
    public ResultResponse getTagList() {
        return ResultResponse.success(adultTagsService.getList());
    }

    @GetMapping("/getAuthorList")
    public ResultResponse getAuthorList() {
        return ResultResponse.success(adultAuthorService.getList());
    }

    @PostMapping("/upload")
    public ResultResponse upload(@RequestParam MultipartFile[] files, @RequestParam String folder) {
        String[] formats = {"jpg", "png"};
        String pathName = "/adult/" + folder + "/";

        List<File> list = uploadFile.upload(files, pathName, formats);
        return ResultResponse.success(list.get(0).getPath());
    }

    @PostMapping("/uploads")
    public ResultResponse uploads(@RequestParam MultipartFile[] files, @RequestParam String folder) {
        String[] formats = {"jpg", "png"};
        String pathName = "/adult/" + folder + "/";

        List<File> list = uploadFile.upload(files, pathName, formats);
        return ResultResponse.success(list.stream().map(item -> item.getPath()).toList());
    }

    @GetMapping("/delImg")
    public ResultResponse delImg(@Validated @RequestParam String path) {
        adultService.delImg(path);
        return ResultResponse.success();
    }

    @GetMapping("/bindEmbyId")
    public ResultResponse bindEmbyId(@Validated @RequestParam Integer id, @Validated @RequestParam Integer embyId) {
        adultService.bindEmbyId(id, embyId);
        return ResultResponse.success();
    }
}
