package com.example.resourcemanager.service.comic;

import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.comic.Comic;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public interface ComicService {
    Map<String, Object> getList(int page, int size, int status);

    void addComic(Map<String, Files> map,Integer comicSetID);

    List<Files> createSave(List<Files> files);
}
