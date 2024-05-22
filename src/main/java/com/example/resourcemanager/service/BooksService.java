package com.example.resourcemanager.service;

import com.example.resourcemanager.entity.Books;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BooksService {
    List<Books> getBooksList(int page);

    Integer addBooks(Books books);
}
