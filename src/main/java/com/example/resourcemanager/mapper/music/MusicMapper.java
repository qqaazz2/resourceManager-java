package com.example.resourcemanager.mapper.music;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.dto.music.MusicListItemDTO;
import com.example.resourcemanager.dto.music.MusicListQueryCondition;
import com.example.resourcemanager.entity.music.Music;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MusicMapper extends MPJBaseMapper<Music> {
    List<MusicListItemDTO> getList(MusicListQueryCondition condition);

    Integer count(MusicListQueryCondition condition);
}
