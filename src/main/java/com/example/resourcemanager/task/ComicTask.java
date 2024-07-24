package com.example.resourcemanager.task;

import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.service.comic.ComicService;
import com.example.resourcemanager.service.comic.ComicSetService;
import com.example.resourcemanager.service.impl.comic.ComicSetServiceImpl;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Async
@Component
public class ComicTask extends AsyncTask {
    @Resource
    ComicSetService comicSetService;

    @Resource
    ComicService comicService;

    static Map<String, File> covers = new HashMap<>();
    Map<String,Integer> folders = new HashMap<>();

    public ComicTask() {
        basePath = "comic";
    }

    @Override
    public void create() {
        covers.clear();
        StringBuilder stringBuilder = new StringBuilder();
        File coverFolder = new File(stringBuilder.append(filePath).append(File.separator).append("comic").append(File.separator).append("cover").toString());
        List<Files> files = new ArrayList<>();
        if (!coverFolder.exists()) coverFolder.mkdirs();
        int index = 0;
        for (Files filesData : createFiles) {
            if (filesData.getIsFolder() == 1) {
                File file = Arrays.stream(filesData.getFile().listFiles()).filter(File::isFile).findFirst().orElse(null);
                filesData.setCover(file.getPath());
                System.out.println(file.getAbsolutePath());
                files.add(filesData);
            } else {
                executor.submit(new Task(filesData.getFile(), coverFolder,index));
            }
            index++;
        }

        executor.shutdown();
        try {
            while (executor.isTerminated()) {
                if(!covers.isEmpty())files.stream().forEach(value -> value.setCover(covers.get(value.getCover()).getPath()));
                files = comicSetService.createSave(files);
                folders.putAll(files.stream().collect(Collectors.toMap(Files::getFilePath,Files::getId)));
                createFiles.stream().filter(value -> value.getIsFolder() == 2).forEach(value -> value.setParentId(folders.get(value.getFile().getParent())));
                createFiles.removeIf(value -> value.getIsFolder() == 1);
                comicService.createSave(createFiles);
            }
        } catch (Exception e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            e.printStackTrace();
            throw new BizException("4000", "漫画文件扫描失败");
        }
    }
}

@AllArgsConstructor
class Task extends Thread {
    File file;
    File coverFolder;
    Integer index;
    @Override
    public void run() {
        String name = DigestUtils.md5DigestAsHex((file.getName() + new Date().getTime()).getBytes());
        File coverFile = new File(coverFolder + File.separator + name + ".jpg");
        Integer entryIndex = 1;
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
             OutputStream outputStream = new FileOutputStream(coverFolder + File.separator + coverFile)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (FilesUtils.isImageFile(entry.getName())) {
                    if(entryIndex == 1){
                        byte[] bytes = new byte[1024];
                        int n;

                        for (; ; ) {
                            n = zipInputStream.read(bytes);
                            if (n < 0) break;
                            outputStream.write(bytes, 0, n);
                        }
                        outputStream.flush();
                    }
                    entryIndex++;
                }
            }
        } catch (Exception e) {
            throw new BizException("4000", "漫画封面提取失败", e);
        }
        ComicTask.covers.put(file.getPath(), coverFile);
        ComicTask.createFiles.get(index).setCover(coverFile.getPath());
        ComicTask.createFiles.get(index).setOther(entryIndex);
    }
}
