package com.example.resourcemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.mapper.FilesMapper;
import com.example.resourcemanager.service.FilesService;
import com.example.resourcemanager.util.FilesUtils;
import com.example.resourcemanager.util.ValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class FilesServiceImpl extends ServiceImpl<FilesMapper, Files> implements FilesService {

    @Override
    public List<Files> getByType(Integer type) {
        LambdaQueryWrapper<Files> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Files::getType, type);
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<Files> renameFiles(List<Files> files) {
        this.updateBatchById(files);
        return files;
    }

    @Override
    public List<Files> createFiles(List<Files> files) {
        boolean isTrue = this.saveBatch(files);
        System.out.println(isTrue);
        if (!isTrue) throw new BizException("4000", "创建文件数据失败");
        return files;
    }

    @Override
    public void removerFiles(List<Files> files) {
        this.removeBatchByIds(files);
    }

    @Override
    public void rename(Integer id, String name) {
        LambdaUpdateWrapper<Files> updateWrapper = new LambdaUpdateWrapper<Files>();
        updateWrapper.eq(Files::getId, id).set(Files::getModifiableName, name);
        ValidationUtils.checkCondition(this.update(updateWrapper),"文件重命名失败");
    }
}
