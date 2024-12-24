package com.example.resourcemanager.controller.comic;

import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.common.UploadFile;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.QueryCondition;
import com.example.resourcemanager.dto.comic.ComicSetDetailDTO;
import com.example.resourcemanager.dto.comic.ComicSetListQueryCondition;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.comic.ComicSet;
import com.example.resourcemanager.service.comic.ComicService;
import com.example.resourcemanager.service.comic.ComicSetService;
import com.example.resourcemanager.task.ComicTask;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Update;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comic")
public class ComicController {
    @Resource
    ComicSetService comicSetService;

    @Resource
    ComicService comicService;

    @Resource
    ComicTask comicTask;

    @GetMapping("/scanning")
    public ResultResponse upload(@RequestParam(required = false, defaultValue = "") String path) {
        comicTask.start(path);
        return ResultResponse.success();
    }

    @GetMapping("/getList")
    public ResultResponse getList(ComicSetListQueryCondition queryCondition) {
        return ResultResponse.success(comicSetService.getList(queryCondition));
    }

    @GetMapping("/setLove")
    public ResultResponse setLove(@RequestParam Integer id, @RequestParam Integer love) {
        comicSetService.setLove(id, love);
        return ResultResponse.success();
    }

    @GetMapping("/getDetail")
    public ResultResponse getDetail(@RequestParam Integer id) {
        return ResultResponse.success(comicSetService.getDetail(id));
    }

    @PostMapping("/updateData")
    public ResultResponse updateData(@Validated({Update.class}) @RequestBody ComicSetDetailDTO comicSet) {
        comicSetService.updateData(comicSet);
        return ResultResponse.success();
    }

    @GetMapping("/getPageList")
    public ResultResponse getPageList(@RequestParam String path) {
        return ResultResponse.success(comicService.getPageList(path));
    }

    @GetMapping("/getComicList")
    public ResultResponse getComicList(ComicSetListQueryCondition queryCondition) {
        return ResultResponse.success(comicService.getList(queryCondition));
    }

    @GetMapping("/updateNumber")
    public ResultResponse updateNumber(@RequestParam Integer id, @RequestParam Integer num, @RequestParam Boolean over, @RequestParam Integer filesId) {
        return ResultResponse.success(comicService.updateNumber(id, num, over, filesId));
    }

    @PreAuthorize("@customPermissionEvaluator.hasPermission(authentication,null,1)")
    @GetMapping("/getMysteryComicList")
    public ResultResponse getMysteryComicList(ComicSetListQueryCondition queryCondition) {
        return ResultResponse.success();
//        return ResultResponse.success(comicService.getList(queryCondition));
    }
}
