package com.example.resourcemanager.service.impl.adult;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.adult.*;
import com.example.resourcemanager.entity.adult.*;
import com.example.resourcemanager.mapper.adult.AdultAuthorBindMapper;
import com.example.resourcemanager.mapper.adult.AdultBindMapper;
import com.example.resourcemanager.mapper.adult.AdultMapper;
import com.example.resourcemanager.service.adult.AdultBindService;
import com.example.resourcemanager.service.adult.AdultService;
import com.example.resourcemanager.service.adult.AdultTagsService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdultBindServiceImpl extends ServiceImpl<AdultBindMapper, AdultBind> implements AdultBindService {
    @Value("${file.upload}")
    String filePath;

    @Resource
    AdultTagsService adultTagsService;

    public void saveData(List<AdultTagDTO> list, Integer adultId, Boolean isEdit) {
        List<Integer> ids = adultTagsService.getTagIds(list);
        boolean isSuccess = true;
        List<AdultBind> bindList = new ArrayList<>();
        if (isEdit) {
            LambdaQueryWrapper<AdultBind> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AdultBind::getAdultId, adultId);
            List<AdultBind> adultBinds = this.list(queryWrapper);
            List<Integer> tagIds = adultBinds.stream().map(AdultBind::getAdultTagsId).collect(Collectors.toList());
            ids.removeAll(tagIds);
            tagIds.removeAll(ids);
            if (!tagIds.isEmpty()) {
                queryWrapper.in(AdultBind::getAdultTagsId, tagIds);
                isSuccess = this.remove(queryWrapper);
                if (!isSuccess) throw new BizException("4000", "修改标签失败");
            }
        }
        bindList = ids.stream().map(value -> new AdultBind(value, adultId)).collect(Collectors.toList());
        if(bindList.size() == 0) return;

        isSuccess = this.saveBatch(bindList);
        if (!isSuccess) throw new BizException("4000", "保存标签失败");
    }
}
