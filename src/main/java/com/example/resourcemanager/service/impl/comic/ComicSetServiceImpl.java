package com.example.resourcemanager.service.impl.comic;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.comic.ComicSetDetailDTO;
import com.example.resourcemanager.dto.comic.ComicSetListDTO;
import com.example.resourcemanager.dto.comic.ComicSetListQueryCondition;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.comic.ComicSet;
import com.example.resourcemanager.mapper.comic.ComicSetMapper;
import com.example.resourcemanager.service.FilesService;
import com.example.resourcemanager.service.comic.ComicSetService;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
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
    ComicSetMapper comicSetMapper;

    @Override
    public List<ComicSet> createSave(List<ComicSet> comicSetList) {
        boolean isTrue = this.saveBatch(comicSetList);
        if (!isTrue) throw new BizException("4000", "创建合集信息失败");
        return comicSetList;
    }

    @Override
    public PageVO<ComicSetListDTO> getList(ComicSetListQueryCondition queryCondition) {
        List<ComicSetListDTO> list = comicSetMapper.getList(queryCondition);
        Integer count = comicSetMapper.count(queryCondition);
        return new PageVO(queryCondition.getLimit(), queryCondition.getPage(), count, list);
    }

    @Override
    public void setLove(Integer id, Integer love) {
        LambdaUpdateWrapper<ComicSet> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(ComicSet::getId,id).set(ComicSet::getLove,love);
        boolean isTrue = this.update(updateWrapper);
        if(!isTrue) throw new BizException("4000","更新喜欢状态失败");
    }

    @Override
    public ComicSetDetailDTO getDetail(Integer id) {
        ComicSetDetailDTO comicSetDetailDTO = comicSetMapper.getDetail(id);
        if(comicSetDetailDTO == null) throw new BizException("4000","查询失败，没有该数据");
        return comicSetDetailDTO;
    }

    @Override
    public ComicSetDetailDTO updateData(ComicSetDetailDTO comicSetDetailDTO) {
        System.out.println(comicSetDetailDTO);
        ComicSet comicSet = new ComicSet();
        BeanUtils.copyProperties(comicSetDetailDTO,comicSet);
        boolean isTrue = this.updateById(comicSet);
        if(!isTrue) throw new BizException("4000","修改漫画系列失败");
        return comicSetDetailDTO;
    }
}
