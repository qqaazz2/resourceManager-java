package com.example.resourcemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.entity.books.Books;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BooksMapper extends BaseMapper<Books> {

    @Select("SELECT books.id, books.`name`, author, last_read, books.cover, illustrator, count( books_id ) AS read_num, books.`status`, books.add_time FROM books LEFT JOIN books_details ON books.id = books_id AND books_details.`status` AND books_details.`status` IN (1,3) AND books_details.deleted = 1 GROUP BY books.id ORDER BY ${sortFiled} ${sort} LIMIT #{skip},#{limit}")
    List<Books> getBooksList(int skip,int limit,String sortFiled,String sort);
}
