package com.example.resourcemanager.service;

import com.example.resourcemanager.entity.Books;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface BooksService {
    Map<String,Object> getBooksList(int page,int size);

    Integer addBooks(Books books);

    Integer editBooks(Books books);
}
