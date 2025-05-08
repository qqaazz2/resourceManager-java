package com.example.resourcemanager.service.impl.adult;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.adult.AdultListDTO;
import com.example.resourcemanager.dto.adult.AdultTagDTO;
import com.example.resourcemanager.entity.adult.Adult;
import com.example.resourcemanager.entity.adult.AdultBind;
import com.example.resourcemanager.entity.adult.AdultTags;
import com.example.resourcemanager.mapper.adult.AdultBindMapper;
import com.example.resourcemanager.mapper.adult.AdultTagsMapper;
import com.example.resourcemanager.service.adult.AdultBindService;
import com.example.resourcemanager.service.adult.AdultTagsService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdultTagsServiceImpl extends ServiceImpl<AdultTagsMapper, AdultTags> implements AdultTagsService {
    @Value("${file.upload}")
    String filePath;

    public List<Integer> getTagIds(List<AdultTagDTO> list) {
        List<Integer> existingIds = list.stream()
                .filter(tagDTO -> Objects.nonNull(tagDTO.getId()))
                .map(AdultTagDTO::getId)
                .collect(Collectors.toList());

        List<AdultTags> newTagsToSave = list.stream()
                .filter(tagDTO -> Objects.isNull(tagDTO.getId()))
                .map(tagDTO -> {
                    AdultTags tags = new AdultTags();
                    tags.setName(tagDTO.getName());
                    return tags;
                })
                .collect(Collectors.toList());

        List<Integer> newIds = new ArrayList<>();
        if (!newTagsToSave.isEmpty()) {
            this.saveBatch(newTagsToSave);
            newIds = newTagsToSave.stream()
                    .map(AdultTags::getId)
                    .filter(Objects::nonNull) // 确保获取到id,防止出现null
                    .collect(Collectors.toList());
        }

        List<Integer> allIds = new ArrayList<>(existingIds);
        allIds.addAll(newIds);

        return allIds;
    }

    public List<AdultTagDTO> getList() {
        List<AdultTags> tagsList = this.list();
        List<AdultTagDTO> list = new ArrayList<>();
        for (AdultTags adult : tagsList) {
            AdultTagDTO adultTagDTO = new AdultTagDTO();
            BeanUtils.copyProperties(adult, adultTagDTO); // 使用 BeanUtils 复制属性
            list.add(adultTagDTO);
        }

        return list;
    }

    @Override
    public void delData(Integer id) {
        boolean isSuccess =  this.removeById(id);
        if(!isSuccess) throw new BizException("4000","删除失败");
    }
}
