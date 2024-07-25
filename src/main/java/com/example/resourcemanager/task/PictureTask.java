package com.example.resourcemanager.task;

import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.service.comic.ComicService;
import com.example.resourcemanager.service.picture.PictureService;
import jakarta.annotation.Resource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PictureTask extends AsyncTask {
    @Resource
    PictureService pictureService;

    @Resource
    ComicService comicService;

    static Map<String, File> covers = new HashMap<>();
    Map<String, Integer> folders = new HashMap<>();

    public PictureTask() {
        basePath = "picture";
    }

    @Override
    public void create() {
        List<Files> files = createFiles.stream().filter(value -> value.getIsFolder() == 1).toList();

        files = pictureService.createData(files);
        folders.putAll(files.stream().collect(Collectors.toMap(Files::getFilePath, Files::getId)));
        createFiles.stream().filter(value -> value.getIsFolder() == 2).forEach(value -> value.setParentId(folders.get(value.getFile().getParent())));
        createFiles.removeIf(value -> value.getIsFolder() == 1);
        pictureService.createData(createFiles);
    }
}
