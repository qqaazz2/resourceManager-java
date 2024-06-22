package com.example.resourcemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.entity.books.BooksDetails;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BooksDetailsMapper extends BaseMapper<BooksDetails> {
}
