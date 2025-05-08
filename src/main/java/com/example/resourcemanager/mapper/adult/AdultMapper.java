package com.example.resourcemanager.mapper.adult;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.resourcemanager.dto.adult.AdultDetailDTO;
import com.example.resourcemanager.entity.adult.Adult;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdultMapper extends MPJBaseMapper<Adult> {
    AdultDetailDTO getDetail(Integer id);
}
