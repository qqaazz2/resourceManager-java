package com.example.resourcemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.entity.books.Books;
import com.example.resourcemanager.mapper.BooksDetailsMapper;
import com.example.resourcemanager.mapper.BooksMapper;
import com.example.resourcemanager.service.BooksService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BooksServiceImpl extends ServiceImpl<BooksMapper, Books> implements BooksService {

    @Resource
    BooksDetailsMapper booksDetailsMapper;

    Map<Integer, String> filedMap = Map.of(1, "id", 2, "books.add_time", 3, "books.edit_time", 4, "last_read");


    @Override
    public Map<String, Object> getBooksList(int page, int size, int sortFiled, String sort) {
        String filed = filedMap.get(sortFiled);
        List<Books> books = this.getBaseMapper().getBooksList(page > 1 ? (page - 1) * size : 0, page * size, filed, sort);
        Long count = this.count();
        Map<String, Object> map = new HashMap<>();
        map.put("data", books);
        map.put("count", count);
        return map;
    }

    @Override
    public Integer addBooks(Books books) {
        this.save(books);
        return books.getId();
    }

    @Override
    public Integer editBooks(Books books) {
        this.updateById(books);
        return books.getId();
    }

//    @Override
//    public void editBooksCount(Integer id) {
//        LambdaQueryWrapper<BooksDetails> queryWrapper = new QueryWrapper<BooksDetails>().lambda();
//        queryWrapper.eq(BooksDetails::getBooks_id, id);
//        int count = Math.toIntExact(booksDetailsMapper.selectCount(queryWrapper));
//        Books books = new Books();
//        books.setCount(count);
//        books.setId(id);
//        this.updateById(books);
//    }

    @Override
    public void editBooksCover(Integer id, String url) {
        UpdateWrapper<Books> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("cover", url);
        this.update(updateWrapper);
    }
}
