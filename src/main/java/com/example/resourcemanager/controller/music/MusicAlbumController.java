package com.example.resourcemanager.controller.music;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.QueryCondition;
import com.example.resourcemanager.dto.music.MusicAlbumListQueryCondition;
import com.example.resourcemanager.dto.music.MusicListQueryCondition;
import com.example.resourcemanager.service.music.MusicAlbumService;
import com.example.resourcemanager.service.music.MusicService;
import com.example.resourcemanager.task.MusicTask;
import com.example.resourcemanager.util.HlsUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/musicAlbum")
public class MusicAlbumController {
    @Resource
    MusicAlbumService musicAlbumService;

    @GetMapping("/getAlbumList")
    public ResultResponse getAlbumList(MusicAlbumListQueryCondition queryCondition) {
        return ResultResponse.success(musicAlbumService.getAlbumList(queryCondition));
    }

    @GetMapping("/getAlbumDetail")
    public ResultResponse getAlbumDetail(@RequestParam Integer id) {
        return ResultResponse.success(musicAlbumService.getAlbumDetail(id));
    }
}
