package com.example.resourcemanager.service.impl.comic;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.MetaData;
import com.example.resourcemanager.entity.books.BooksDetails;
import com.example.resourcemanager.entity.comic.Comic;
import com.example.resourcemanager.entity.comic.ComicSet;
import com.example.resourcemanager.mapper.comic.ComicMapper;
import com.example.resourcemanager.service.FilesService;
import com.example.resourcemanager.service.comic.ComicService;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Service
@Transactional(rollbackFor = Exception.class)
public class ComicServiceImpl extends ServiceImpl<ComicMapper, Comic> implements ComicService {

    @Resource
    FilesUtils filesUtils;

    @Value("${file.upload}")
    String filePath;

    @Resource
    FilesService filesService;

    @Override
    public Map<String, Object> getList(int page, int size, int status) {
        LambdaQueryWrapper<Comic> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(status != 0, Comic::getStatus, status);
        List<Comic> list = this.page(new Page(page, size), lambdaQueryWrapper).getRecords();
        Long count = this.count(lambdaQueryWrapper);
        return Map.of("data", list, "count", count);
    }

    @Override
    public void addComic(Map<String, Files> map, Integer comicSetID) {

    }

    @Override
    public List<Files> createSave(List<Files> files) {
        files = filesService.createFiles(files);
        List<Comic> comicList = files.stream().map(item -> {
            Comic comic = new Comic();
            comic.setCover(item.getCover());
            comic.setFilesId(item.getId());
            comic.setTotal((Integer) item.getOther());
            return comic;
        }).collect(Collectors.toList());

        boolean isTrue = this.saveBatch(comicList);
        if (!isTrue) throw new BizException("4000", "创建漫画信息失败");
        return files;
    }

//    @Override
//    public void addComic(Map<String, Files> map,Integer comicSetID) {
//        List<Comic> list = new ArrayList<>();
//        ExecutorService executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(20));
//        List<File> fileList = new ArrayList<>();
//        List<Callable<File>> callableList = new ArrayList<>();
//        for (String key : map.keySet()) {
//            Callable<File> fileCallable = new Task(map.get(key));
//            callableList.add(fileCallable);
//        }
//
//        try {
//            List<Future<File>> futureList = executor.invokeAll(callableList);
//            for (Future<File> future:futureList) {
//                if(future.get() != null){
//                    fileList.add(future.get());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BizException("4000", "漫画封面提取失败");
//        }
//
//        Map<String, Files> coverMap = filesUtils.saveDataBase(fileList,2);
//        for (String key:map.keySet()) {
//            Files files = map.get(key);
//            Comic comic = new Comic();
//            comic.setName(files.getFileName());
//            comic.setCover(coverMap.get(key).getFileName());
//            comic.setUrl(files.getFilePath());
//            comic.setSize((int) files.getFile().length());
//            comic.setComicSetId(comicSetID);
//            try {
//                comic.setTotal(new ZipFile(files.getFile()).size());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            list.add(comic);
//        }
//
//        if(!this.saveBatch(list)) new BizException("4000","漫画新增失败");
//    }

    @AllArgsConstructor
    class Task implements Callable<File> {
        Files files;

        @Override
        public File call() {
            File file = files.getFile();
            File newFile = new File(file.getParent() + "/cover/" + file.getName() + ".jpg");
            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file)); OutputStream outputStream = new FileOutputStream(newFile)) {
                ZipEntry entry = zipInputStream.getNextEntry();
                if (entry != null && isImageFile(entry.getName())) {
                    byte[] bytes = new byte[1024];
                    int n;

                    for (; ; ) {
                        n = zipInputStream.read(bytes);
                        if (n < 0) break;
                        outputStream.write(bytes);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizException("4000", "漫画封面提取失败");
            }
            return newFile;
        }

        private static boolean isImageFile(String fileName) {
            String lowerCaseFileName = fileName.toLowerCase();
            return lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg") ||
                    lowerCaseFileName.endsWith(".png") || lowerCaseFileName.endsWith(".gif") ||
                    lowerCaseFileName.endsWith(".bmp") || lowerCaseFileName.endsWith(".tiff") ||
                    lowerCaseFileName.endsWith(".webp");
        }
    }
}
