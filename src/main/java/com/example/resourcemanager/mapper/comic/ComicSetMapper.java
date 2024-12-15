package com.example.resourcemanager.mapper.comic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.dto.book.BookListDTO;
import com.example.resourcemanager.dto.book.BookListQueryCondition;
import com.example.resourcemanager.dto.comic.ComicSetDetailDTO;
import com.example.resourcemanager.dto.comic.ComicSetListDTO;
import com.example.resourcemanager.dto.comic.ComicSetListQueryCondition;
import com.example.resourcemanager.entity.comic.ComicSet;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ComicSetMapper extends BaseMapper<ComicSet> {
    List<ComicSetListDTO> getList(ComicSetListQueryCondition queryCondition);

    Integer count(ComicSetListQueryCondition queryCondition);

    ComicSetDetailDTO getDetail(Integer id);
}
