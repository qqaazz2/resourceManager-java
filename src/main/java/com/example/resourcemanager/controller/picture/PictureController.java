package com.example.resourcemanager.controller.picture;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.dto.QueryCondition;
import com.example.resourcemanager.dto.picture.PictureDetailDTO;
import com.example.resourcemanager.dto.picture.PictureQueryCondition;
import com.example.resourcemanager.mapper.picture.PictureMapper;
import com.example.resourcemanager.service.picture.PictureService;
import com.example.resourcemanager.task.PictureTask;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/picture")
public class PictureController {
    @Resource
    PictureTask pictureTask;

    @Resource
    PictureService pictureService;

    @GetMapping("scanning")
    public ResultResponse scanning(@RequestParam(defaultValue = "", required = false) String path) {
        pictureTask.startOrRestart(path);
        return ResultResponse.success();
    }

    @GetMapping("getFolderList")
    public ResultResponse getFolderList(PictureQueryCondition queryCondition) {
        return ResultResponse.success(pictureService.getFolderList(queryCondition));
    }

    @GetMapping("getTimeLineList")
    public ResultResponse getTimeLineList(PictureQueryCondition queryCondition) {
        return ResultResponse.success(pictureService.getTimeLineList(queryCondition));
    }

    @GetMapping("getRandList")
    public ResultResponse getRandList(Integer limit) {
        return ResultResponse.success(pictureService.getRandList(limit));
    }

    @PostMapping("setDisplay")
    public ResultResponse setDisplay(@RequestBody Map<String,Integer> body) {
        pictureService.setDisplay(body.get("id"), body.get("display"));
        return ResultResponse.success();
    }

    @PostMapping("setLove")
    public ResultResponse setLove(@RequestBody Map<String,Integer> body) {
        pictureService.setLove(body.get("id"), body.get("love"));
        return ResultResponse.success();
    }

    @PostMapping("editData")
    public ResultResponse editData(@Validated(Update.class) @RequestBody PictureQueryCondition pictureQueryCondition) {
        pictureService.editData(pictureQueryCondition);
        return ResultResponse.success();
    }
}
