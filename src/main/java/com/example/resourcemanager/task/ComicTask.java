package com.example.resourcemanager.task;

import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.book.Book;
import com.example.resourcemanager.entity.book.Series;
import com.example.resourcemanager.entity.comic.Comic;
import com.example.resourcemanager.entity.comic.ComicSet;
import com.example.resourcemanager.service.comic.ComicService;
import com.example.resourcemanager.service.comic.ComicSetService;
import com.example.resourcemanager.service.impl.comic.ComicSetServiceImpl;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
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
    public static Map<Integer, ComicFolder> folderMap = new HashMap<>();
    public static Files coverFiles = new Files();
    public List<Files> comicList = new ArrayList<>();
    public List<Comic> comics = new ArrayList<>();
    public static List<Files> coverList = new ArrayList<>();
    public static List<ComicSet> comicSetList = new ArrayList<>();

    public ComicTask() {
        basePath = "comic";
        contentType = 2;
    }

    @Override
    public void create() {
        covers.clear();
        coverList.clear();
        comicSetList.clear();
        comics.clear();
        folderMap.clear();
        comicList.clear();

        createCover();

        createFiles.removeIf(value -> value.getFileName().equals("cover"));
        createFiles.removeIf(value -> value.getFileType().equals("image/jpeg"));


        deepCreate(createFiles, 1);

        List<Future<Comic>> futureList = new ArrayList<>();
        ExecutorService executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(200));
        for (Files files : comicList) {
            Future<Comic> future = executor.submit(new GetComicTask(files, filesUtils));
            futureList.add(future);
        }

        for (Future<Comic> future : futureList) {
            try {
                Comic comic = future.get();
                comics.add(comic);
            } catch (Exception e) {
                e.printStackTrace();
                executor.shutdownNow();
                future.cancel(true);
                throw new BizException("4000", "EPUB文件识别失败，请重试");
            }
        }
        if (!executor.isShutdown()) {
            executor.shutdownNow();
        }
        coverList = filesService.createFiles(coverList);
        Map<String, Integer> map = coverList.stream().collect(Collectors.toMap(Files::getFileName, Files::getId));
        for (Comic comic : comics) {
            Integer coverId = null;
            if (map.containsKey(comic.getHash() + ".jpg")) {
                coverId = map.get(comic.getHash() + ".jpg");
                comic.setCover(coverId);
            }
        }

        for (ComicFolder comicFolder : folderMap.values()) {
            ComicSet comicSet = new ComicSet();
            comicSet.setFilesId(comicFolder.getId());
            comicSet.setName(comicFolder.getName());
            if (comicFolder.getHash() != null) {
                if (map.containsKey(comicFolder.getHash() + ".jpg")) {
                    Integer coverId = map.get(comicFolder.getHash() + ".jpg");
                    comicSet.setCover(coverId);
                }
            }
            comicSetList.add(comicSet);
        }

        if (comicSetList.size() > 0) comicSetService.createSave(comicSetList);
        comicService.createSave(comics);
    }

    public void deepCreate(List<Files> list, Integer index) {
        list = filesService.createFiles(list);
        for (Files files : list) {
            if (files.getIsFolder() == 2) {
                comicList.add(files);
            } else {
                ComicFolder comicFolder = new ComicFolder();
                comicFolder.setId(files.getId());
                comicFolder.setName(files.getFileName());
                folderMap.put(files.getId(), comicFolder);
            }

            if (files.getChild() == null) continue;
            List<Files> childes = files.getChild().stream().peek(value -> value.setParentId(files.getId())).toList();
            deepCreate(childes, index += 1);
        }
    }

    public void createCover() {
        File file = new File(filePath + resourcesPath + File.separator + "cover");
        Files files = new Files();
        files.setFileName("cover");
        files.setParentId(-1);
        files.setType(contentType);
        files.setIsFolder(1);
        coverFiles = filesService.getFiles(files);

        if (!file.exists()) {
            file.mkdirs();
            filesUtils.checkMetaFile(file);
        }

        if (coverFiles == null) {
            coverFiles = filesUtils.createFolder(file, -1, contentType, 0);
            coverFiles = filesService.createFile(coverFiles);
        }
    }
}

@AllArgsConstructor
class GetComicTask implements Callable<Comic> {
    Files files;
    FilesUtils filesUtils;

    @Override
    public Comic call() {
        File cover = new File(ComicTask.coverFiles.getFilePath() + File.separator + files.getHash() + ".jpg");
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(files.getFile()));
             OutputStream outputStream = new FileOutputStream(cover)) {
            ZipEntry entry;
            int fileCount = 0;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    if(fileCount == 0) zipInputStream.transferTo(outputStream);
                    fileCount++;
                }
                zipInputStream.closeEntry();  // 关闭当前条目
            }

            Comic comic = new Comic();
            comic.setFilesId(files.getId());
            comic.setTotal(fileCount);
            comic.setHash(files.getHash());
            comic.setParentId(files.getParentId());

            if (files.getSort() == 0 && ComicTask.folderMap.containsKey(files.getParentId())) {
                ComicFolder comicFolder = ComicTask.folderMap.get(files.getParentId());
                comicFolder.setHash(files.getHash());
                ComicTask.folderMap.put(files.getParentId(), comicFolder);
            }

            ComicTask.coverList.add(filesUtils.createFiles(cover, 0, ComicTask.coverFiles.getId(), files.getSort()));

            return comic;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("漫画文件不存在");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("文件解析错误");
        }
        return null;
    }
}

@Data
class ComicFolder {
    private Integer id;
    private String name;
    private String hash;
}