package com.example.resourcemanager.controller.book;

import com.example.resourcemanager.common.ResultResponse;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.book.*;
import com.example.resourcemanager.entity.book.Book;
import com.example.resourcemanager.service.book.BookService;
import com.example.resourcemanager.task.BookTask;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/book")
public class BookController {
    @Resource
    BookTask bookTask;

    @Resource
    BookService bookService;

    @GetMapping("scanning")
    public ResultResponse scanning(@RequestParam(defaultValue = "", required = false) String path) {
        bookTask.start(path);
        return ResultResponse.success();
    }

    @GetMapping("getList")
    public ResultResponse getList(BookListQueryCondition queryCondition) {
        return ResultResponse.success(bookService.getList(queryCondition));
    }

    @PostMapping("updateData")
    public ResultResponse updateData(@RequestBody @Validated({Update.class}) Book book) {
        bookService.updateData(book);
        return ResultResponse.success();
    }

    @PostMapping("updateProgress")
    public ResultResponse updateProgress(@RequestBody @Validated({Update.class}) BookListDTO book) {
        Map<String, Object> map = bookService.updateProgress(book);
        return ResultResponse.success(map);
    }

    @GetMapping("getCoverList")
    public ResultResponse getCoverList(Integer id) {
        Map<String, List<BookCoverDTO>> map = new HashMap<>();
        map.put("list",bookService.getCoverList(id));
        return ResultResponse.success(map);
    }
}
