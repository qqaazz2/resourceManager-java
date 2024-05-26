package com.example.resourcemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.entity.Books;
import com.example.resourcemanager.mapper.BooksMapper;
import com.example.resourcemanager.service.BooksService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BooksServiceImpl extends ServiceImpl<BooksMapper, Books> implements BooksService {
    @Override
    public Map<String,Object> getBooksList(int page,int size) {
        LambdaQueryWrapper<Books> queryWrapper = new QueryWrapper<Books>().lambda();
        List<Books> books = this.page(new Page<>(page, size),queryWrapper).getRecords();
        Long count = this.count(queryWrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("data",books);
        map.put("count",count);
        return map;
    }

    @Override
    public Integer addBooks(Books books) {
        this.save(books);
        return books.getId();
    }
}
