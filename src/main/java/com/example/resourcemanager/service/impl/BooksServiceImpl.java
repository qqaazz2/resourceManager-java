package com.example.resourcemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.entity.Books;
import com.example.resourcemanager.mapper.BooksMapper;
import com.example.resourcemanager.service.BooksService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BooksServiceImpl extends ServiceImpl<BooksMapper, Books> implements BooksService {

    public static Integer size = 10;

    @Override
    public List<Books> getBooksList(int page) {
        LambdaQueryWrapper<Books> queryWrapper = new QueryWrapper<Books>().lambda();
        List<Books> books = this.page(new Page<>(page, size),queryWrapper).getRecords();
        return books;
    }

    @Override
    public Integer addBooks(Books books) {
        this.save(books);
        return books.getId();
    }
}
