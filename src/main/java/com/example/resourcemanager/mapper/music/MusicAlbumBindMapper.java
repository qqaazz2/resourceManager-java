package com.example.resourcemanager.mapper.music;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.entity.music.MusicAlbum;
import com.example.resourcemanager.entity.music.MusicAlbumBind;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MusicAlbumBindMapper extends MPJBaseMapper<MusicAlbumBind> {
}
