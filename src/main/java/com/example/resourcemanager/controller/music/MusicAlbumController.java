package com.example.resourcemanager.controller.music;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.dto.logs.music.MusicAlbumListQueryCondition;
import com.example.resourcemanager.service.music.MusicAlbumService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
