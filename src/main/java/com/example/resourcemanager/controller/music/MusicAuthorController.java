package com.example.resourcemanager.controller.music;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.dto.logs.music.MusicAuthorListQueryCondition;
import com.example.resourcemanager.service.music.MusicAuthorService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/musicAuthor")
public class MusicAuthorController {
    @Resource
    MusicAuthorService musicAuthorService;

    @GetMapping("/getAuthorList")
    public ResultResponse getList(MusicAuthorListQueryCondition condition){
        return ResultResponse.success(musicAuthorService.getList(condition));
    }

    @GetMapping("/getAuthorDetail")
    public ResultResponse getAuthorDetail(@RequestParam Integer id){
        return ResultResponse.success(musicAuthorService.getDetails(id));
    }
}
