package com.example.resourcemanager.task;

import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.service.FilesService;
import com.example.resourcemanager.service.picture.PictureService;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Async
@Component
public class PictureTask extends AsyncTask {
    @Resource
    PictureService pictureService;

    @Resource
    FilesService filesService;

    List<Files> pictureList = new ArrayList();
    public PictureTask() {
        basePath = "picture";
        contentType = 3;
    }

    @Override
    public void create() {
        pictureList.clear();

        deepCreate(createFiles,1);
        pictureService.createData(pictureList);
    }

    public void deepCreate(List<Files> list, Integer index) {
        list = filesService.createFiles(list);
        for (Files files : list) {
            if(files.getIsFolder() == 2) pictureList.add(files);
            if (files.getChild() == null) continue;
            List<Files> childes = files.getChild().stream().peek(value -> value.setParentId(files.getId())).toList();
            deepCreate(childes, index += 1);
        }
    }
}
