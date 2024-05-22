package com.example.resourcemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.entity.Books;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BooksMapper extends BaseMapper<Books> {
}
