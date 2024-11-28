package com.example.resourcemanager.task;

import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.service.FilesService;
import com.example.resourcemanager.service.picture.PictureService;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Async
@Component
public class PictureTask extends AsyncTask {
    @Resource
    PictureService pictureService;

    @Resource
    FilesService filesService;

    public PictureTask() {
        basePath = "picture";
        contentType = 3;
    }

    @Override
    public void create() {
        List<Files> filesList = createFiles.stream().filter(value -> value.getIsFolder() == 1).map(value -> {
            File[] files = value.getFile().listFiles();
            if (files == null) {
                return value;
            }

            for (File file : files) {
                String path = file.getPath();
                if (FilesUtils.isImageFile(path)) {
                    value.setCover(path);
                    break;
                }
            }
            return value;
        }).collect(Collectors.toList());
        if(!filesList.isEmpty()){
            filesList = filesService.createFiles(filesList);
            getChildren(filesList);
        }
        pictureService.createData(createFiles);
    }
}
