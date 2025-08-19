package com.example.resourcemanager.service;

import com.example.resourcemanager.entity.books.Books;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public interface BooksService {
    Map<String,Object> getBooksList(int page,int size,int sortFile,String sort);

    Integer addBooks(Books books);

    Integer editBooks(Books books);

    void editBooksCover(Integer id,String url);
}
