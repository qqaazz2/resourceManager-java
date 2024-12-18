package com.example.resourcemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.books.BooksDetails;
import com.example.resourcemanager.mapper.BooksDetailsMapper;
import com.example.resourcemanager.service.BooksDetailsService;
import com.example.resourcemanager.service.BooksService;
import jakarta.annotation.Resource;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class BooksDetailsServiceImpl extends ServiceImpl<BooksDetailsMapper, BooksDetails> implements BooksDetailsService {
    @Value("${file.upload}")
    String filePath;

    @Resource
    BooksDetailsMapper booksDetailsMapper;

    @Resource
    BooksService booksService;

    static int count;

    @Override
    public Map<String, Object> getDetailsList(Integer id, Integer page, Integer size) {
        LambdaQueryWrapper<BooksDetails> queryWrapper = new QueryWrapper<BooksDetails>().lambda();
        queryWrapper.eq(!(id.equals(0)), BooksDetails::getBooks_id, id);
        queryWrapper.orderByAsc(BooksDetails::getSort);
        List<BooksDetails> list = this.page(new Page(page, size), queryWrapper).getRecords();
        long count = this.count(queryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("data", list);
        map.put("count", count);
        return map;
    }

    AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    @Override
    public Boolean addDetails(Integer booksID, List<File> list) {
        atomicBoolean.set(false);
        count = Math.toIntExact(booksDetailsMapper.selectCount(new UpdateWrapper<BooksDetails>().eq("books_id", booksID)));
        ExecutorService executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(1000));
        List<Callable<BooksDetails>> callableList = new ArrayList<>();
        List<BooksDetails> booksDetailsList = new ArrayList<>();
        int index = 0;

        for (File file : list) {
            Callable<BooksDetails> callable = new Task(file, index += 1, booksID);
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
        boolean type = this.saveBatch(booksDetailsList);
        if (count == 0) {
            System.out.println(465);
            BooksDetails booksDetails = booksDetailsList.get(0);
            booksService.editBooksCover(booksDetails.getBooks_id(), booksDetails.getCover());
        }
        return type;
    }

    @Override
    public Boolean deleteDetails(List<Integer> ids, Integer bookID, boolean type) {

        File file;
        File cover;
        if (type) {
            List<BooksDetails> list = this.listByIds(ids);
            for (BooksDetails details : list) {
                file = new File(filePath + File.separator + details.getUrl());
                cover = new File(filePath + File.separator + details.getCover());
                file.delete();
                cover.delete();
            }
        }
        boolean isDel = this.removeBatchByIds(ids);
        return isDel;
    }

    @Override
    public void editDetails(BooksDetails booksDetails) {
        int status = booksDetails.getStatus();
        if(status == 1){
            booksDetails.setProgress((float) 0);
        }else if(status == 2){
            booksDetails.setProgress((float) 1);
        }
        boolean isTrue = this.updateById(booksDetails);
        if(!isTrue) throw new BizException("4000", "书籍信息修改失败");
    }

    @Override
    public void changeProgress(Integer id, Float progress) {
        LambdaUpdateWrapper<BooksDetails> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(BooksDetails::getId,id).set(BooksDetails::getProgress,progress);
        boolean isTrue = this.update(lambdaUpdateWrapper);
        if(!isTrue) throw new BizException("4000", "阅读记录保存失败");
    }

    @Override
    public BooksDetails getDetails(Integer id) {
        LambdaQueryWrapper<BooksDetails> queryWrapper = new QueryWrapper<BooksDetails>().lambda();
        queryWrapper.eq(BooksDetails::getId, id);
        return this.getOne(queryWrapper);
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

            String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
            File coverFile = new File(folder + File.separator + "cover" + File.separator + fileName + ".jpg");
            if (!coverFile.exists()) coverFile.mkdirs();
            try (InputStream inputStream = new FileInputStream(file)) {
                EpubReader epubReader = new EpubReader();
                Book book = epubReader.readEpub(inputStream);

                InputStream coverInputStream = book.getCoverImage().getInputStream();
                BufferedImage coverImage = ImageIO.read(coverInputStream);
                ImageIO.write(coverImage, "jpg", coverFile);

                booksDetails.setBooks_id(booksID);
                booksDetails.setCover("books/" + folder.getName() + "/cover/" + fileName + ".jpg");
                booksDetails.setUrl("books/" + folder.getName() + File.separator + file.getName());
                booksDetails.setName(fileName);
                booksDetails.setSort(count + index);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("获取epub文件封面错误");
            }
            return booksDetails;
        }
    }
}
