package com.example.resourcemanager.service.impl.picture;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.comic.ComicSet;
import com.example.resourcemanager.entity.picture.Picture;
import com.example.resourcemanager.mapper.picture.PictureMapper;
import com.example.resourcemanager.service.FilesService;
import com.example.resourcemanager.service.picture.PictureService;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Resource
    FilesService filesService;

    @Override
    public List<Files> createData(List<Files> files) {
        files = filesService.createFiles(files);
        List<Picture> pictureList = files.stream().map(item -> {
            Picture picture = new Picture();
            try {
                picture.setFilesId(item.getId());
                BufferedImage bufferedImage = ImageIO.read(item.getFile());
                int height = bufferedImage.getHeight();
                int width = bufferedImage.getWidth();
                picture.setHeight(height);
                picture.setWidth(width);
                picture.setMp(FilesUtils.getImgMp(width,height));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return picture;
        }).collect(Collectors.toList());

        boolean isTrue = this.saveBatch(pictureList);
        if (!isTrue) throw new BizException("4000", "创建图片信息失败");
        return files;
    }
}
