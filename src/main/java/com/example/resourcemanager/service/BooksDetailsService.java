package com.example.resourcemanager.service;

import com.example.resourcemanager.entity.BooksDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public interface BooksDetailsService {
    List<BooksDetails> getDetailsList(Integer id);

    Boolean addDetails(Integer booksID,List<File> list);
}
