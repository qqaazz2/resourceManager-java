package com.example.resourcemanager.service.book;

import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.book.BookCoverDTO;
import com.example.resourcemanager.dto.book.BookListDTO;
import com.example.resourcemanager.dto.book.BookListQueryCondition;
import com.example.resourcemanager.entity.book.Book;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface BookService {
    List<Book> createData(List<Book> list);

    PageVO<BookListDTO> getList(BookListQueryCondition queryCondition);

    Book updateData(Book book);

    Map<String,Object> updateProgress(BookListDTO bookListDTO);

    List<BookCoverDTO> getCoverList(Integer id);

    BookListDTO getRecent();

    Map<String,Integer> getOverview();

    Map<String,String> changeCover(Integer id,MultipartFile file);
}
