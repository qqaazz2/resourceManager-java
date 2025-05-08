package com.example.resourcemanager.mapper.adult;

import com.example.resourcemanager.dto.adult.AdultDetailDTO;
import com.example.resourcemanager.entity.adult.Adult;
import com.example.resourcemanager.entity.adult.AdultAuthor;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdultAuthorMapper extends MPJBaseMapper<AdultAuthor> {
}
