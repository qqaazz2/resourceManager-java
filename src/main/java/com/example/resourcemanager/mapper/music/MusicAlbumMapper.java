package com.example.resourcemanager.mapper.music;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.entity.music.MusicAlbum;
import com.example.resourcemanager.entity.music.MusicAuthor;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MusicAlbumMapper extends MPJBaseMapper<MusicAlbum> {
}
