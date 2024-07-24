package com.example.resourcemanager.mapper.comic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.entity.comic.Comic;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ComicMapper extends BaseMapper<Comic> {
    List<Comic> getList();
}
