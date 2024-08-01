package com.example.resourcemanager.service.impl.picture;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.picture.PictureDetailDTO;
import com.example.resourcemanager.dto.picture.PictureItemDTO;
import com.example.resourcemanager.dto.picture.PictureQueryCondition;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.picture.Picture;
import com.example.resourcemanager.mapper.picture.PictureMapper;
import com.example.resourcemanager.service.FilesService;
import com.example.resourcemanager.service.picture.PictureService;
import com.example.resourcemanager.util.FilesUtils;
import com.example.resourcemanager.util.ValidationUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Resource
    FilesService filesService;

    @Resource
    PictureMapper pictureMapper;

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
                picture.setCreateTime(new Date(item.getFile().lastModified()));
                picture.setMp(FilesUtils.getImgMp(width, height));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return picture;
        }).collect(Collectors.toList());

        boolean isTrue = this.saveBatch(pictureList);
        if (!isTrue) throw new BizException("4000", "创建图片信息失败");
        return files;
    }

    @Override
    public PageVO<PictureDetailDTO> getFolderList(PictureQueryCondition queryCondition) {
        List<PictureDetailDTO> list = pictureMapper.getList(queryCondition);
        Integer count = pictureMapper.count(queryCondition);
        return new PageVO(queryCondition.getLimit(), queryCondition.getPage(), count, list);
    }

    @Override
    public PageVO<PictureDetailDTO> getTimeLineList(PictureQueryCondition queryCondition) {
        List<PictureDetailDTO> list = pictureMapper.getTimeLineList(queryCondition);
        Integer count = pictureMapper.countTimeLineList(queryCondition);
        return new PageVO(queryCondition.getLimit(), queryCondition.getPage(), count, list);
    }

    @Override
    public List<PictureDetailDTO> getRandList(Integer limit) {
        return pictureMapper.getRandList(limit);
    }

    @Override
    public void setDisplay(Integer id,Integer display) {
        LambdaUpdateWrapper<Picture> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Picture::getId,id).set(Picture::getDisplay,display);
        ValidationUtils.checkCondition(this.update(updateWrapper),"保存查看状态失败");
    }

    @Override
    public void setLove(Integer id, Integer love) {
        LambdaUpdateWrapper<Picture> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Picture::getId,id).set(Picture::getLove,love);
        ValidationUtils.checkCondition(this.update(updateWrapper),"收藏失败");
    }
}
