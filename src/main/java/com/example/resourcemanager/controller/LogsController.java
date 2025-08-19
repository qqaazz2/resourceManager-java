package com.example.resourcemanager.controller;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.dto.QueryCondition;
import com.example.resourcemanager.dto.logs.music.LogListQueryCondition;
import com.example.resourcemanager.service.LoggingEventService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogsController {
    @Resource
    LoggingEventService loggingEventService;

    @GetMapping("/getLogList")
    public ResultResponse getLogList(LogListQueryCondition condition) {
        return ResultResponse.success(loggingEventService.getLogList(condition));
    }
}
