package com.example.resourcemanager.service.impl.music;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.logs.music.AuthorItem;
import com.example.resourcemanager.dto.logs.music.MusicData;
import com.example.resourcemanager.dto.logs.music.MusicListQueryCondition;
import com.example.resourcemanager.entity.music.MusicAuthor;
import com.example.resourcemanager.entity.music.MusicAuthorBind;
import com.example.resourcemanager.entity.music.MusicInfo;
import com.example.resourcemanager.mapper.music.MusicAuthorBindMapper;
import com.example.resourcemanager.mapper.music.MusicInfoMapper;
import com.example.resourcemanager.service.music.MusicInfoService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MusicInfoServiceImpl extends ServiceImpl<MusicInfoMapper, MusicInfo> implements MusicInfoService {

    @Resource
    MusicAuthorBindMapper musicAuthorBindMapper;

    @Override
    public PageVO<MusicData> getMusicInfoList(MusicListQueryCondition queryCondition) {

        LambdaQueryWrapper<MusicInfo> lambdaQueryWrapper = checkQueryCondition(queryCondition);
        List<MusicInfo> musicInfoList;
        if (queryCondition.isAll()) {
            musicInfoList = this.list(lambdaQueryWrapper);
        } else {
            musicInfoList = this.page(new Page(queryCondition.getPage(), queryCondition.getLimit()), lambdaQueryWrapper).getRecords();
        }
        long count = this.count(lambdaQueryWrapper);

        List<Integer> ids = musicInfoList.stream().map(MusicInfo::getId).collect(Collectors.toList());
        Map<Integer, List<AuthorItem>> authorsByMusicId = getAuthorList(ids);
        List<MusicData> musicDataList = new ArrayList<>();
        musicInfoList.forEach(item -> {
            MusicData musicData = new MusicData();
            BeanUtils.copyProperties(item, musicData);
            if (authorsByMusicId.containsKey(item.getId())) {
                musicData.setAuthorItems(authorsByMusicId.get(item.getId()));
            }
            musicDataList.add(musicData);
        });

            return new PageVO<>(queryCondition.getLimit(), queryCondition.getPage(), (int) count, musicDataList);
    }

    private LambdaQueryWrapper<MusicInfo> checkQueryCondition(MusicListQueryCondition queryCondition) {
        LambdaQueryWrapper<MusicInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(queryCondition.getAlbum() != null, MusicInfo::getAlbumId, queryCondition.getAlbum());
        lambdaQueryWrapper.eq(queryCondition.getLove() != null, MusicInfo::getLove, queryCondition.getLove());
        System.out.println(queryCondition.getAuthor());
        if (queryCondition.getAuthor() != null) {
            LambdaQueryWrapper<MusicAuthorBind> bindLambdaQueryWrapper = new LambdaQueryWrapper<>();
            bindLambdaQueryWrapper.eq(MusicAuthorBind::getAuthorId, queryCondition.getAuthor());
            bindLambdaQueryWrapper.select(MusicAuthorBind::getMusicId);
            List<MusicAuthorBind> list = musicAuthorBindMapper.selectList(bindLambdaQueryWrapper);
            System.out.println("list");
            System.out.println(list);
            if (!list.isEmpty())
                lambdaQueryWrapper.in(MusicInfo::getId, list.stream().map(MusicAuthorBind::getMusicId).collect(Collectors.toList()));
        }
        return lambdaQueryWrapper;
    }

    private Map<Integer, List<AuthorItem>> getAuthorList(List<Integer> ids) {
        MPJLambdaWrapper<MusicAuthorBind> authorBindMPJLambdaWrapper = new MPJLambdaWrapper<MusicAuthorBind>().in(MusicAuthorBind::getMusicId, ids)
                .select(MusicAuthor::getId, MusicAuthor::getName, MusicAuthor::getCover)
                .selectAs(MusicAuthorBind::getMusicId, AuthorItem::getBindId)
                .leftJoin(MusicAuthor.class, MusicAuthor::getId, MusicAuthorBind::getAuthorId);
        List<AuthorItem> musicAuthors = musicAuthorBindMapper.selectJoinList(AuthorItem.class, authorBindMPJLambdaWrapper);
        Map<Integer, List<AuthorItem>> authorsByMusicId = musicAuthors.stream()
                .collect(Collectors.groupingBy(AuthorItem::getBindId));
        return authorsByMusicId;
    }
}
