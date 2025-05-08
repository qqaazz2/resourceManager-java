package com.example.resourcemanager.controller.music;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.dto.music.MusicListQueryCondition;
import com.example.resourcemanager.service.music.MusicInfoService;
import com.example.resourcemanager.service.music.MusicService;
import com.example.resourcemanager.task.MusicTask;
import com.example.resourcemanager.util.FilesUtils;
import com.example.resourcemanager.util.HlsUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;

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

    @Resource
    HlsUtils hlsUtils;

    @GetMapping("scanning")
    public ResultResponse scanning(@RequestParam(defaultValue = "", required = false) String path) {
        musicTask.start(path);
        return ResultResponse.success();
    }

    @GetMapping("getMusicList")
    public ResultResponse getMusicList(MusicListQueryCondition queryCondition) {
        return ResultResponse.success(musicService.getMusicList(queryCondition));
    }

    @GetMapping("convertToM3U8")
    public ResultResponse convertToM3U8() {
        boolean isTrue = hlsUtils.submitTask(filePath + File.separator + "music/1.flac", filePath + File.separator + "music/cache");
        return ResultResponse.success(isTrue);
    }

    @GetMapping("/getM3U8")
    public ResultResponse getM3U8(@RequestParam String outputDir) {
        return ResultResponse.success(hlsUtils.getIsDone());
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
