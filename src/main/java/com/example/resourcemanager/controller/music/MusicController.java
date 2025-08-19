package com.example.resourcemanager.controller.music;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.dto.logs.music.MusicListQueryCondition;
import com.example.resourcemanager.service.music.MusicInfoService;
import com.example.resourcemanager.service.music.MusicService;
import com.example.resourcemanager.task.MusicTask;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/music")
public class MusicController {
    @Resource
    MusicTask musicTask;

    @Value("${file.upload}")
    String filePath;

    @Resource
    MusicService musicService;

    @Resource
    MusicInfoService musicInfoService;

    @GetMapping("scanning")
    public ResultResponse scanning(@RequestParam(defaultValue = "", required = false) String path) {
        musicTask.startOrRestart(path);
        return ResultResponse.success();
    }

    @GetMapping("getMusicList")
    public ResultResponse getMusicList(MusicListQueryCondition queryCondition) {
        return ResultResponse.success(musicService.getMusicList(queryCondition));
    }

    @GetMapping("getRandom")
    public ResultResponse getRandom() {
        return ResultResponse.success(musicService.getRandom());
    }

    @GetMapping("getMusicItemsList")
    public ResultResponse getMusicInfoList(MusicListQueryCondition queryCondition) {
        return ResultResponse.success(musicInfoService.getMusicInfoList(queryCondition));
    }
}
