package com.example.resourcemanager.service.impl.book;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.book.BookCoverDTO;
import com.example.resourcemanager.dto.book.BookListDTO;
import com.example.resourcemanager.dto.book.BookListQueryCondition;
import com.example.resourcemanager.entity.book.Book;
import com.example.resourcemanager.mapper.book.BookMapper;
import com.example.resourcemanager.service.book.BookService;
import com.example.resourcemanager.service.book.SeriesService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
    @Resource
    BookMapper bookMapper;

    @Resource
    SeriesService seriesService;

    @Override
    public List<Book> createData(List<Book> list) {
        Boolean isTrue = this.saveBatch(list);
        if (!isTrue) throw new BizException("4000", "新增书籍信息失败");
        return list;
    }

    @Override
    public PageVO<BookListDTO> getList(BookListQueryCondition queryCondition) {
        List<BookListDTO> list = bookMapper.getList(queryCondition);
        Integer count = bookMapper.count(queryCondition);
        return new PageVO(queryCondition.getLimit(), queryCondition.getPage(), count, list);
    }

    @Override
    public Book updateData(Book book) {
        Boolean isTrue = this.updateById(book);
        if (!isTrue) throw new BizException("4000", "新增书籍信息失败");
        return book;
    }

    @Override
    public Map<String, Object> updateProgress(BookListDTO bookListDTO) {
        int status = bookListDTO.getProgress() >= 1 ? 2 : 3;
        LambdaUpdateWrapper<Book> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(Book::getFilesId, bookListDTO.getId()).set(Book::getStatus, status).set(Book::getProgress, bookListDTO.getProgress()).set(Book::getReadTagNum,bookListDTO.getReadTagNum());
        Boolean isTrue = this.update(updateWrapper);
        if (!isTrue) throw new BizException("4000", "阅读进度修改失败");
        Map<String, Object> map = new HashMap<>();

        bookListDTO.setStatus(status);

        map.put("book", bookListDTO);
        map.put("status", false);

        if (status == 2) {
            List<BookListDTO> list = bookMapper.getListByParentId(bookListDTO.getParentId());
            Long count = list.stream().filter(value -> value.getStatus() != 2).count();
            if (count == 0) {
                seriesService.updateStatus(bookListDTO.getParentId(), 2);
                map.put("status", true);
            }
        }
        return map;
    }

    @Override
    public List<BookCoverDTO> getCoverList(Integer id) {
        return bookMapper.getCoverList(id);
    }
}
