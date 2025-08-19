package com.example.resourcemanager.service;

import com.example.resourcemanager.dto.logs.LogDTO;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.QueryCondition;
import com.example.resourcemanager.dto.logs.music.LogListQueryCondition;
import org.springframework.stereotype.Service;

@Service
public interface LoggingEventService {
    PageVO<LogDTO> getLogList(LogListQueryCondition condition);

}
