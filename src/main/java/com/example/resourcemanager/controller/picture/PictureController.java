package com.example.resourcemanager.controller.picture;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.dto.QueryCondition;
import com.example.resourcemanager.dto.picture.PictureQueryCondition;
import com.example.resourcemanager.mapper.picture.PictureMapper;
import com.example.resourcemanager.service.picture.PictureService;
import com.example.resourcemanager.task.PictureTask;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/picture")
public class PictureController {
    @Resource
    PictureTask pictureTask;

    @Resource
    PictureService pictureService;

    @GetMapping("scanning")
    public ResultResponse scanning(@RequestParam(defaultValue = "", required = false) String path) {
        pictureTask.start(path);
        return ResultResponse.success();
    }

    @GetMapping("getFolderList")
    public ResultResponse getFolderList(PictureQueryCondition queryCondition) {
        return ResultResponse.success(pictureService.getFolderList(queryCondition));
    }

    @GetMapping("getRandList")
    public ResultResponse getRandList(Integer limit) {
        return ResultResponse.success(pictureService.getRandList(limit));
    }

    @PostMapping("setDisplay")
    public ResultResponse setDisplay(@RequestParam Integer id, @RequestParam(required = false, defaultValue = "2") Integer display) {
        pictureService.setDisplay(id, display);
        return ResultResponse.success();
    }

    @PostMapping("setLove")
    public ResultResponse setLove(@RequestParam Integer id, @RequestParam Integer love) {
        pictureService.setLove(id, love);
        return ResultResponse.success();
    }
}
