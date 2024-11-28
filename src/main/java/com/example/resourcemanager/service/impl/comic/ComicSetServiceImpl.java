package com.example.resourcemanager.service.impl.comic;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.comic.ComicSet;
import com.example.resourcemanager.mapper.comic.ComicSetMapper;
import com.example.resourcemanager.service.FilesService;
import com.example.resourcemanager.service.comic.ComicSetService;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Transactional(rollbackFor = Exception.class)
public class ComicSetServiceImpl extends ServiceImpl<ComicSetMapper, ComicSet> implements ComicSetService {
    @Resource
    FilesService filesService;

    @Override
    public List<Files> createSave(List<Files> files) {
        files = filesService.createFiles(files);
        List<ComicSet> comicSetList = files.stream().map(item -> {
            ComicSet comicSet = new ComicSet();
            comicSet.setFilesId(item.getId());
            return comicSet;
        }).collect(Collectors.toList());

        boolean isTrue = this.saveBatch(comicSetList);
        if (!isTrue) throw new BizException("4000", "创建合集信息失败");
        return files;
    }
}
