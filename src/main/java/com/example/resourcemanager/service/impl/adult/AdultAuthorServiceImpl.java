package com.example.resourcemanager.service.impl.adult;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.adult.AdultAuthorDTO;
import com.example.resourcemanager.entity.adult.AdultAuthor;
import com.example.resourcemanager.entity.adult.AdultTags;
import com.example.resourcemanager.mapper.adult.AdultAuthorMapper;
import com.example.resourcemanager.mapper.adult.AdultTagsMapper;
import com.example.resourcemanager.service.adult.AdultAuthorService;
import com.example.resourcemanager.service.adult.AdultTagsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdultAuthorServiceImpl extends ServiceImpl<AdultAuthorMapper, AdultAuthor> implements AdultAuthorService {
    @Value("${file.upload}")
    String filePath;

    @Override
    public List<AdultAuthorDTO> getList() {
        List<AdultAuthor> authorList = this.list();
        List<AdultAuthorDTO> list = new ArrayList<>();
        for (AdultAuthor adultAuthor : authorList) {
            AdultAuthorDTO dto = new AdultAuthorDTO();
            BeanUtils.copyProperties(adultAuthor, dto);
            list.add(dto);
        }
        return list;
    }

    @Override
    public void addData(AdultAuthorDTO authorDTO) {
        AdultAuthor adultAuthor = new AdultAuthor();
        BeanUtils.copyProperties(authorDTO,adultAuthor);
        boolean isSuccess = this.save(adultAuthor);

        if(!isSuccess) throw new BizException("4000","新增失败");
    }

    @Override
    public void editData(AdultAuthorDTO authorDTO) {
        AdultAuthor adultAuthor = new AdultAuthor();
        BeanUtils.copyProperties(authorDTO,adultAuthor);
        boolean isSuccess = this.updateById(adultAuthor);

        if(!isSuccess) throw new BizException("4000","修改失败");
    }

    @Override
    public void delData(Integer id) {
        boolean isSuccess =  this.removeById(id);
        if(!isSuccess) throw new BizException("4000","删除失败");
    }
}
