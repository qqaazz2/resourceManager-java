package com.example.resourcemanager.service;

import com.example.resourcemanager.entity.books.BooksDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public interface BooksDetailsService {
    Map<String,Object> getDetailsList(Integer id, Integer page, Integer size);

    Boolean addDetails(Integer booksID,List<File> list);

    Boolean deleteDetails(List<Integer> ids,Integer bookID,boolean type);

    void editDetails(BooksDetails booksDetails);

    void changeProgress(Integer id,Float progress);

    BooksDetails getDetails(Integer id);
}
