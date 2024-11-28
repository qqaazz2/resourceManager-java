package com.example.resourcemanager.mapper.book;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.dto.QueryCondition;
import com.example.resourcemanager.dto.book.SeriesListDTO;
import com.example.resourcemanager.dto.book.SeriesListQueryCondition;
import com.example.resourcemanager.dto.picture.PictureDetailDTO;
import com.example.resourcemanager.entity.book.Series;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SeriesMapper extends BaseMapper<Series> {
    List<SeriesListDTO> getList(SeriesListQueryCondition queryCondition);

    Integer count(SeriesListQueryCondition queryCondition);

    SeriesListDTO getOne(Integer id);
}
