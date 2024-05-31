package com.example.resourcemanager.service.impl;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.Books;
import com.example.resourcemanager.entity.BooksDetails;
import com.example.resourcemanager.mapper.BooksDetailsMapper;
import com.example.resourcemanager.service.BooksDetailsService;
import com.example.resourcemanager.service.BooksService;
import lombok.AllArgsConstructor;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class BooksDetailsServiceImpl extends ServiceImpl<BooksDetailsMapper, BooksDetails> implements BooksDetailsService {
    @Value("${file.upload}")
    String filePath;


    @Override
    public List<BooksDetails> getDetailsList(Integer id) {
        LambdaQueryWrapper<BooksDetails> queryWrapper = new QueryWrapper<BooksDetails>().lambda();
        queryWrapper.eq(!(id.equals(0)), BooksDetails::getBooks_id, id);
        List<BooksDetails> list = this.list(queryWrapper);
        return list;
    }

    AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    @Override
    public Boolean addDetails(Integer booksID, List<File> list) {
        atomicBoolean.set(false);
        ExecutorService executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(20));
        List<Callable<BooksDetails>> callableList = new ArrayList<>();
        List<BooksDetails> booksDetailsList = new ArrayList<>();
        int index = 0;

        for (File file : list) {
            Callable<BooksDetails> callable = new Task(file, index++, booksID);
            callableList.add(callable);
        }

        try {
            List<Future<BooksDetails>> futureList = executor.invokeAll(callableList);

            for (Future<BooksDetails> future : futureList) {
                try {
                    BooksDetails booksDetails = future.get();
                    if (booksDetails != null) {
                        booksDetailsList.add(booksDetails);
                    }
                } catch (Exception e) {
                    System.out.println(111111111);
                    atomicBoolean.set(true);
                    executor.shutdownNow();
                    e.printStackTrace();
                    throw new BizException("4000", "epub封面处理异常");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("4000", "新增图书时多线程提交任务失败");
        } finally {
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
        }

        return this.saveBatch(booksDetailsList);
    }

    // 处理epub文件的文件上传
    @AllArgsConstructor
    class Task implements Callable<BooksDetails> {
        File file;
        Integer index;
        Integer booksID;

        @Override
        public BooksDetails call() throws Exception {
            if (atomicBoolean.get()) return null;

            BooksDetails booksDetails = new BooksDetails();
            File folder = file.getParentFile();

            File coverFile = new File(folder + File.separator + "cover" + File.separator + index + ".jpg");
            if (!coverFile.exists()) coverFile.mkdirs();
            try (InputStream inputStream = new FileInputStream(file)) {
                EpubReader epubReader = new EpubReader();
                Book book = epubReader.readEpub(inputStream);

                InputStream coverInputStream = book.getCoverImage().getInputStream();
                BufferedImage coverImage = ImageIO.read(coverInputStream);
                ImageIO.write(coverImage, "jpg", coverFile);

                booksDetails.setBooks_id(booksID);
                booksDetails.setCover("books/" + folder.getName() + "/cover/" + index + ".jpg");
                booksDetails.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));
                booksDetails.setSort(index);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("获取epub文件封面错误");
            }
            return booksDetails;
        }
    }
}
