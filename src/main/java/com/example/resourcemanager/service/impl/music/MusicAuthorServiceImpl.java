package com.example.resourcemanager.service.impl.music;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.logs.music.*;
import com.example.resourcemanager.entity.music.*;
import com.example.resourcemanager.mapper.music.MusicAuthorBindMapper;
import com.example.resourcemanager.mapper.music.MusicAuthorMapper;
import com.example.resourcemanager.mapper.music.MusicMapper;
import com.example.resourcemanager.service.music.MusicAuthorService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicAuthorServiceImpl extends ServiceImpl<MusicAuthorMapper, MusicAuthor> implements MusicAuthorService {

    @Resource
    MusicMapper musicMapper;

    @Resource
    MusicAuthorBindMapper musicAuthorBindMapper;

    @Override
    public List<MusicAuthor> getList() {
        return this.list();
    }

    @Override
    public List<MusicAuthor> createData(List<MusicAuthor> list) {
        boolean isSuccess = this.saveBatch(list);
        if (!isSuccess) throw new BizException("创建音乐艺术家失败");
        return list;
    }

    @Override
    public PageVO<MusicAuthor> getList(MusicAuthorListQueryCondition queryCondition) {
        List<MusicAuthor> list = this.page(new Page(queryCondition.getPage(), queryCondition.getLimit())).getRecords();
        long count = this.count();
        return new PageVO(queryCondition.getLimit(), queryCondition.getPage(), (int) count, list);
    }

    @Override
    public AuthorDetailDTO getDetails(Integer id) {
        MusicAuthor musicAuthor = this.getById(id);

        MPJLambdaWrapper<MusicAuthorBind> queryWrapper = new MPJLambdaWrapper<MusicAuthorBind>()
                .eq(MusicAuthor::getId, id)
                .eq(Music::getLove,2)
                .leftJoin(Music.class, Music::getId, MusicBind::getMusicId)
                .leftJoin(MusicAuthor.class, MusicAuthor::getId, MusicAuthorBind::getAuthorId);
        long count = musicAuthorBindMapper.selectJoinCount(queryWrapper);
        AuthorDetailDTO authorDTO = new AuthorDetailDTO();
        BeanUtils.copyProperties(musicAuthor, authorDTO);
        authorDTO.setLoveNum((int) count);

        return authorDTO;
    }
}
