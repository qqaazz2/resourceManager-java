package com.example.resourcemanager.task;

import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.book.Book;
import com.example.resourcemanager.entity.book.Series;
import com.example.resourcemanager.service.FilesService;
import com.example.resourcemanager.service.book.BookService;
import com.example.resourcemanager.service.book.SeriesService;
import com.example.resourcemanager.service.picture.PictureService;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.epub.EpubReader;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Async
@Component
public class BookTask extends AsyncTask {
    @Resource
    BookService bookService;

    @Resource
    SeriesService seriesService;

    @Resource
    FilesService filesService;

    public static List<Files> coverList = new ArrayList<>();
    public static List<Files> epubList = new ArrayList<>();
    public static Integer parentId = -1;
    public static Files coverFiles = new Files();
    public List<Book> bookList = new ArrayList<>();
    public List<Series> seriesList = new ArrayList<>();
    public Map<Integer, Integer> updateSeries = new HashMap<>();
    public Map<Integer, Files> folderMap = new HashMap<>();

    public BookTask() {
        basePath = "books";
        contentType = 1;
    }

    @Override
    @Transactional
    public void create() {
        coverList.clear();
        epubList.clear();
        folderMap.clear();
        bookList.clear();
        seriesList.clear();
        updateSeries.clear();

        createFiles.removeIf(value -> value.getFileName().equals("cover"));
        createFiles.removeIf(value -> value.getFileType().equals("image/jpeg"));
        if (createFiles.size() == 0) return;
        ;

        createCover();

        deepCreate(createFiles, 1);
        List<Future<Book>> futureList = new ArrayList<>();
        ExecutorService executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(200));
        for (Files files : epubList) {
            Future<Book> future = executor.submit(new GetBookCoverTask(files, filesUtils));
            futureList.add(future);
        }

        for (Future<Book> future : futureList) {
            try {
                System.out.println("运行中");
                Book book = future.get();
                bookList.add(book);
            } catch (Exception e) {
                System.out.println("error");
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
        List<Integer> list = new ArrayList<>();
        for (Book book : bookList) {
            Integer coverId = null;
            if (map.containsKey(book.getHash() + ".jpg")) {
                coverId = map.get(book.getHash() + ".jpg");
                book.setCoverId(coverId);
            }

            if (!list.contains(book.getParentId()) && folderMap.size() > 0) {
                System.out.println(folderMap);
                System.out.println(book.getParentId());
                Series series = new Series();
                series.setFilesId(book.getParentId());
                if (coverId != null) {
                    series.setCoverId(coverId);
                }
                series.setName(folderMap.get(book.getParentId()).getFileName());
                series.setAuthor(book.getAuthor());
                series.setProfile(book.getProfile());
                series.setNum(folderMap.get(book.getParentId()).getFileSize());
                seriesList.add(series);
                list.add(book.getParentId());
            }
        }

        if (seriesList.size() > 0) seriesService.createData(seriesList);
        bookService.createData(bookList);

        updateSeries.forEach((key, value) -> {
            seriesService.updateNum(key, value);
        });
    }

    public void deepCreate(List<Files> list, Integer index) {
        list = filesService.createFiles(list);
        for (Files files : list) {
            if (files.getIsFolder() == 1) folderMap.put(files.getId(), files);
            Boolean isTrue = files.getFileName().substring(files.getFileName().lastIndexOf(".") + 1).toLowerCase().equals("epub");
            if (files.getIsFolder() == 2 && isTrue) {
                epubList.add(files);

                if (files.getParentId() != -1 && index == 1) {
                    Integer num = files.getFile().getParentFile().list().length;
                    updateSeries.put(files.getParentId(), num);
                }
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
class GetBookCoverTask implements Callable<Book> {
    Files files;
    FilesUtils filesUtils;

    @Override
    public Book call() {
        Book book = new Book();
        try (InputStream inputStream = new FileInputStream(files.getFile())) {
            EpubReader epubReader = new EpubReader();
            nl.siegmann.epublib.domain.Book epubBook = epubReader.readEpub(inputStream);

            Metadata metadata = epubBook.getMetadata();
            StringBuilder stringBuilder = new StringBuilder();
            for (Author author : metadata.getAuthors()) {
                stringBuilder.append(author.getFirstname() + author.getLastname());
            }
            book.setAuthor(stringBuilder.toString());
            StringBuilder publisherBuilder = new StringBuilder();
            for (String publisher : metadata.getPublishers()) {
                publisherBuilder.append(publisher);
            }
            book.setPublishing(publisherBuilder.toString());
            if (metadata.getDescriptions().size() > 0) book.setProfile(metadata.getDescriptions().get(0));
            book.setFilesId(files.getId());
            book.setHash(files.getHash());
            book.setParentId(files.getParentId());
            book.setName(files.getFileName());
            try {
                if (epubBook.getCoverImage() == null) return book;
                byte[] data = epubBook.getCoverImage().getData();
                File cover = new File(BookTask.coverFiles.getFilePath() + File.separator + files.getHash() + ".jpg");
                if (!cover.exists()) {
                    try (OutputStream outputStream = new FileOutputStream(cover)) {
                        outputStream.write(data);
                    }
                }
                BookTask.coverList.add(filesUtils.createFiles(cover, 0, BookTask.coverFiles.getId(),0));
            } catch (Exception e) {
                return book;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }
}


