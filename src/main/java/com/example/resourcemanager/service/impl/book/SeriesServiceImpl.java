package com.example.resourcemanager.service.impl.book;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.book.SeriesListDTO;
import com.example.resourcemanager.dto.book.SeriesListQueryCondition;
import com.example.resourcemanager.entity.book.Series;
import com.example.resourcemanager.mapper.book.SeriesMapper;
import com.example.resourcemanager.service.book.SeriesService;
import com.example.resourcemanager.util.ValidationUtils;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SeriesServiceImpl extends ServiceImpl<SeriesMapper, Series> implements SeriesService {
    @Resource
    SeriesMapper seriesMapper;

    @Override
    public List<Series> createData(List<Series> list) {
        Boolean isTrue = this.saveBatch(list);
        if (!isTrue) throw new BizException("4000", "新增系列信息失败");
        return list;
    }

    @Override
    public void updateNum(Integer filesId, Integer num) {
        LambdaUpdateWrapper<Series> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(Series::getFilesId, filesId).set(Series::getNum, num);
        Boolean isTrue = this.update(updateWrapper);
        if (!isTrue) throw new BizException("4000", "修改系列数量失败");
    }

    @Override
    public PageVO<SeriesListDTO> getList(SeriesListQueryCondition seriesListQueryCondition) {
        List<SeriesListDTO> list = seriesMapper.getList(seriesListQueryCondition);
        Integer count = seriesMapper.count(seriesListQueryCondition);
        return new PageVO(seriesListQueryCondition.getLimit(), seriesListQueryCondition.getPage(), count, list);
    }

    @Override
    public void updateLove(Integer id, Integer love) {
        LambdaUpdateWrapper<Series> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(Series::getId, id).set(Series::getLove, love);
        Boolean isTrue = this.update(updateWrapper);
        if (!isTrue) throw new BizException("4000", "修改喜欢状态失败");
    }

    @Override
    public void updateStatus(Integer id, Integer status) {
        LambdaUpdateWrapper<Series> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(Series::getFilesId, id).set(Series::getStatus, status);
        Boolean isTrue = this.update(updateWrapper);
        if (!isTrue) throw new BizException("4000", "修改阅读状态失败");
    }

    @Override
    public Series updateData(Series series) {
        series.setLastReadTime(null);
        Boolean isTrue = this.updateById(series);
        if (!isTrue) throw new BizException("4000", "修改系列信息");
        return series;
    }

    @Override
    public SeriesListDTO getDetails(Integer id) {
        SeriesListDTO seriesListDTO = seriesMapper.getOne(id);
        if (seriesListDTO == null) throw new BizException("4000", "系列信息数据不存在");
        return seriesListDTO;
    }

    @Override
    public void updateCover(Integer id, Integer coverId) {
        LambdaUpdateWrapper<Series> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(Series::getId, id).set(Series::getCoverId, coverId);
        Boolean isTrue = this.update(updateWrapper);
        if (!isTrue) throw new BizException("4000", "修改系列封面失败");
    }

    @Override
    public Date updateLastReadTime(Integer id) {
        Date date = new Date();
        LambdaUpdateWrapper<Series> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(Series::getId, id).set(Series::getLastReadTime, date);
        Boolean isTrue = this.update(updateWrapper);
        if (!isTrue) throw new BizException("4000", "更新最后一次阅读时间失败");

        return date;
    }
}
