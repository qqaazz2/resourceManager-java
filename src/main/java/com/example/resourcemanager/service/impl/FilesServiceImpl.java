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
@Transactional
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
        updateWrapper.eq(Files::getId, id).set(Files::getFileName, name);
        ValidationUtils.checkCondition(this.update(updateWrapper), "文件重命名失败");
    }

    @Override
    public Files getFiles(Files files) {
        LambdaQueryWrapper<Files> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(files.getType() != null, Files::getType, files.getType());
        lambdaQueryWrapper.eq(files.getIsFolder() != null, Files::getIsFolder, files.getIsFolder());
        lambdaQueryWrapper.eq(files.getParentId() != null, Files::getParentId, files.getParentId());
        lambdaQueryWrapper.eq(!files.getFileName().isEmpty(), Files::getFileName, files.getFileName());
        return this.getOne(lambdaQueryWrapper);
    }

    @Override
    public Files createFile(Files files) {
        boolean isTrue = this.save(files);
        if (!isTrue) throw new BizException("4000", "创建文件数据失败");
        return files;
    }
}
