package com.example.resourcemanager.service.impl.adult;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.adult.AdultAuthorDTO;
import com.example.resourcemanager.entity.adult.AdultAuthor;
import com.example.resourcemanager.entity.adult.AdultAuthorBind;
import com.example.resourcemanager.entity.adult.AdultBind;
import com.example.resourcemanager.mapper.adult.AdultAuthorBindMapper;
import com.example.resourcemanager.mapper.adult.AdultAuthorMapper;
import com.example.resourcemanager.mapper.adult.AdultBindMapper;
import com.example.resourcemanager.service.adult.AdultAuthorBindService;
import com.example.resourcemanager.service.adult.AdultBindService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdultAuthorBindServiceImpl extends ServiceImpl<AdultAuthorBindMapper, AdultAuthorBind> implements AdultAuthorBindService {
    @Value("${file.upload}")
    String filePath;

    public void addData(List<AdultAuthorDTO> authorDTOList, Integer adultId) {
        List<AdultAuthorBind> list = authorDTOList.stream().map(value -> {
            AdultAuthorBind adultAuthorBind = new AdultAuthorBind();
            adultAuthorBind.setAdultAuthorId(value.getId());
            adultAuthorBind.setAdultId(adultId);
            return adultAuthorBind;
        }).collect(Collectors.toList());
        boolean isSuccess = this.saveBatch(list);
        if (!isSuccess) throw new BizException("4000", "创建影片信息失败");
    }


    public void editData(List<AdultAuthorDTO> authorDTOList, Integer adultId) {
        LambdaQueryWrapper<AdultAuthorBind> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdultAuthorBind::getAdultId, adultId);
        List<AdultAuthorBind> existingBinds = this.list(queryWrapper);

        List<Integer> existingAuthorIds = existingBinds.stream()
                .map(AdultAuthorBind::getAdultAuthorId)
                .collect(Collectors.toList());

        List<Integer> newAuthorIds = authorDTOList.stream()
                .map(AdultAuthorDTO::getId)
                .collect(Collectors.toList());

        // 解绑逻辑
        List<Integer> authorsToRemove = existingAuthorIds.stream()
                .filter(authorId -> !newAuthorIds.contains(authorId))
                .collect(Collectors.toList());

        if (!authorsToRemove.isEmpty()) {
            queryWrapper.in(AdultAuthorBind::getAdultAuthorId, authorsToRemove);
            if (!this.remove(queryWrapper)) {
                throw new BizException("4000", "解绑演员失败");
            }
        }

        // 绑定逻辑
        List<AdultAuthorBind> bindsToAdd = newAuthorIds.stream()
                .filter(authorId -> !existingAuthorIds.contains(authorId))
                .map(authorId -> {
                    AdultAuthorBind bind = new AdultAuthorBind();
                    bind.setAdultAuthorId(authorId);
                    bind.setAdultId(adultId);
                    return bind;
                })
                .collect(Collectors.toList());

        if (!bindsToAdd.isEmpty() && !this.saveBatch(bindsToAdd)) {
            throw new BizException("4000", "绑定演员失败");
        }
    }
}
