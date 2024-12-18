package com.example.resourcemanager.service.impl.comic;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.comic.ComicListDTO;
import com.example.resourcemanager.dto.comic.ComicSetListQueryCondition;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.MetaData;
import com.example.resourcemanager.entity.books.BooksDetails;
import com.example.resourcemanager.entity.comic.Comic;
import com.example.resourcemanager.entity.comic.ComicSet;
import com.example.resourcemanager.mapper.FilesMapper;
import com.example.resourcemanager.mapper.comic.ComicMapper;
import com.example.resourcemanager.mapper.comic.ComicSetMapper;
import com.example.resourcemanager.service.FilesService;
import com.example.resourcemanager.service.comic.ComicService;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.Charset;
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
    private File tempFolder = new File("/files/comicImages");

    @Resource
    FilesUtils filesUtils;

    @Value("${file.upload}")
    String filePath;

    @Resource
    ComicMapper comicMapper;

    @Resource
    ComicSetMapper comicSetMapper;

    @Resource
    FilesMapper filesMapper;

    @Override
    public PageVO<ComicListDTO> getList(ComicSetListQueryCondition queryCondition) {
        List<ComicListDTO> list = comicMapper.getList(queryCondition);
        Integer count = comicMapper.count(queryCondition);
        return new PageVO(queryCondition.getLimit(), queryCondition.getPage(), count, list);
    }

    @Override
    public void addComic(Map<String, Files> map, Integer comicSetID) {

    }

    @Override
    public List<Comic> createSave(List<Comic> comicList) {
        boolean isTrue = this.saveBatch(comicList);
        if (!isTrue) throw new BizException("4000", "创建漫画信息失败");
        return comicList;
    }

    @Override
    public List<String> getPageList(String path) {
        System.out.println(path);
        return extractImagesToTempFolder(path);
    }

    private List<String> extractImagesToTempFolder(String cbzFilePath) {
        List<String> list = new ArrayList<>();
        if (tempFolder.exists()) {
            try {
                FileUtils.cleanDirectory(tempFolder);
            } catch (IOException e) {
                throw new BizException("4000", "清空临时文件夹失败");
            }
        } else {
            tempFolder.mkdirs();
        }
        File zipFile = new File(cbzFilePath);
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile), Charset.forName("GBK"))) {
            ZipEntry zipEntry;
            byte[] buffer = new byte[1024];
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    File file = new File(tempFolder, zipFile.getName().substring(0, zipFile.getName().indexOf(".")) + zipEntry.getName());
                    try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                        int bytesRead;
                        // 使用缓冲区进行批量读取
                        while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    list.add(file.getPath());
                }
                zipInputStream.closeEntry();
            }
            System.out.println(list);
            return list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new BizException("4000", "文件不存在");
        } catch (IOException e) {
            throw new BizException("4000", "解压漫画文件失败");
        }
    }

    public Boolean updateNumber(Integer id, Integer num, Boolean over, Integer filesId) {
        System.out.println(over);
        LambdaUpdateWrapper<Comic> comicUpdateWrapper = new LambdaUpdateWrapper<>();
        comicUpdateWrapper.eq(Comic::getId, id).set(Comic::getStatus, over ? 2 : 3).set(Comic::getNumber, num);
        boolean isTrue = this.update(comicUpdateWrapper);
        if (!isTrue) throw new BizException("4000", "修改漫画阅读进度失败");

        LambdaUpdateWrapper<ComicSet> comicSetLambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        LambdaQueryWrapper<Files> filesLambdaQueryWrapper = new LambdaQueryWrapper();
        filesLambdaQueryWrapper.eq(Files::getId,filesId).select(Files::getParentId);
        Files files = filesMapper.selectOne(filesLambdaQueryWrapper);

        comicSetLambdaUpdateWrapper.eq(ComicSet::getFilesId, files.getParentId());
        if (over) {

            LambdaQueryWrapper<Files> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(Files::getParentId, files.getParentId()).eq(Files::getIsFolder, 2).select(Files::getId);
            List<Files> list = filesMapper.selectList(lambdaQueryWrapper);
            List<Integer> ids = list.stream().map(value -> value.getId()).collect(Collectors.toList());

            LambdaQueryWrapper<Comic> comicLambdaQueryWrapper = new LambdaQueryWrapper<>();
            comicLambdaQueryWrapper.in(Comic::getFilesId, ids).select(Comic::getStatus);
            List<Comic> comicList = this.list(comicLambdaQueryWrapper);
            List<Comic> collect = comicList.stream().filter(value -> value.getStatus() != 2).collect(Collectors.toList());

            int status = 3;
            if (collect.isEmpty()) {
                status = 2;
            }

            comicSetLambdaUpdateWrapper.set(ComicSet::getReadStatus, status);
            comicSetMapper.update(comicSetLambdaUpdateWrapper);

            return status == 2;
        } else {
            comicSetLambdaUpdateWrapper.set(ComicSet::getReadStatus, 3);
            comicSetMapper.update(comicSetLambdaUpdateWrapper);
            return false;
        }
    }
}
