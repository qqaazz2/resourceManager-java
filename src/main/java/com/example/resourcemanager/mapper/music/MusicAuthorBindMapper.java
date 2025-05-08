package com.example.resourcemanager.mapper.music;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.entity.music.MusicAlbumBind;
import com.example.resourcemanager.entity.music.MusicAuthor;
import com.example.resourcemanager.entity.music.MusicAuthorBind;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MusicAuthorBindMapper extends MPJBaseMapper<MusicAuthorBind> {
}
