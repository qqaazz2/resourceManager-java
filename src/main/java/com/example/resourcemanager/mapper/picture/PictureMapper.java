package com.example.resourcemanager.mapper.picture;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.dto.QueryCondition;
import com.example.resourcemanager.dto.picture.PictureDetailDTO;
import com.example.resourcemanager.dto.picture.PictureItemDTO;
import com.example.resourcemanager.entity.picture.Picture;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PictureMapper extends BaseMapper<Picture> {
    List<PictureItemDTO> getList(QueryCondition queryCondition);

    int count(QueryCondition queryCondition);

    List<PictureDetailDTO> getRandList(Integer limit);
}
