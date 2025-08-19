package com.example.resourcemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.entity.LoggingEvent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoggingEventMapper extends BaseMapper<LoggingEvent> {
}
