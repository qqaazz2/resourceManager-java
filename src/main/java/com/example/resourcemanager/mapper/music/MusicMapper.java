package com.example.resourcemanager.mapper.music;

import com.example.resourcemanager.dto.logs.music.MusicListItemDTO;
import com.example.resourcemanager.dto.logs.music.MusicListQueryCondition;
import com.example.resourcemanager.entity.music.Music;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MusicMapper extends MPJBaseMapper<Music> {
    List<MusicListItemDTO> getList(MusicListQueryCondition condition);

    Integer count(MusicListQueryCondition condition);
}
