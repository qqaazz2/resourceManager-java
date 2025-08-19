package com.example.resourcemanager.service.impl.music;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.logs.music.AlbumDetailDTO;
import com.example.resourcemanager.dto.logs.music.AuthorItem;
import com.example.resourcemanager.dto.logs.music.MusicAlbumListQueryCondition;
import com.example.resourcemanager.dto.logs.music.MusicData;
import com.example.resourcemanager.entity.music.*;
import com.example.resourcemanager.mapper.music.*;
import com.example.resourcemanager.service.music.MusicAlbumService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MusicAlbumServiceImpl extends ServiceImpl<MusicAlbumMapper, MusicAlbum> implements MusicAlbumService {

    @Resource
    MusicAlbumBindMapper musicAlbumBindMapper;

    @Resource
    MusicAuthorBindMapper musicAuthorBindMapper;

    @Resource
    MusicBindMapper musicBindMapper;

    @Resource
    MusicServiceImpl musicService;

    @Override
    public List<MusicAlbum> getList() {
        return this.list();
    }

    @Override
    public List<MusicAlbum> createData(List<MusicAlbum> list) {
        boolean isSuccess = this.saveBatch(list);
        if (!isSuccess) throw new BizException("创建专辑失败");
        return list;
    }

    @Override
    public PageVO<MusicAlbum> getAlbumList(MusicAlbumListQueryCondition condition) {
        List<MusicAlbum> list = this.page(new Page(condition.getPage(), condition.getLimit())).getRecords();
        long count = this.count();
        return new PageVO(condition.getLimit(), condition.getPage(), (int) count, list);
    }

    @Override
    public AlbumDetailDTO getAlbumDetail(Integer id) {
        if (id == -1) {
            return getLoveDetails();
        }else if(id == -3){
            return getRandomDetails();
        }

        MusicAlbum musicAlbum = this.getById(id);
        if (musicAlbum == null) throw new BizException("该专辑不存在");

        MPJLambdaWrapper<MusicBind> queryWrapper = new MPJLambdaWrapper<MusicBind>()
                .eq(MusicBind::getAlbumId, id)
                .select(Music::getId, Music::getFilesId, Music::getTitle, Music::getYear, Music::getLove, Music::getListen, Music::getCover, Music::getSeconds)
                .leftJoin(Music.class, Music::getId, MusicBind::getMusicId);
        List<MusicData> list = musicBindMapper.selectJoinList(MusicData.class, queryWrapper);

        List<Integer> ids = list.stream().map(MusicData::getId).collect(Collectors.toList());
        MPJLambdaWrapper<MusicAuthorBind> authorBindMPJLambdaWrapper = new MPJLambdaWrapper<MusicAuthorBind>().in(MusicAuthorBind::getMusicId, ids)
                .select(MusicAuthor::getId, MusicAuthor::getName, MusicAuthor::getCover)
                .selectAs(MusicAuthorBind::getMusicId, AuthorItem::getBindId)
                .leftJoin(MusicAuthor.class, MusicAuthor::getId, MusicAuthorBind::getAuthorId);
        List<AuthorItem> musicAuthors = musicAuthorBindMapper.selectJoinList(AuthorItem.class, authorBindMPJLambdaWrapper);

        Map<Integer, List<AuthorItem>> authorsByMusicId = musicAuthors.stream()
                .collect(Collectors.groupingBy(AuthorItem::getBindId)); // Assuming AuthorItem has a getMusicId() method

        list = list.stream().map(musicData -> {
            musicData.setAlbumId(id);
            musicData.setAlbumName(musicAlbum.getName());
            musicData.setAuthorItems(authorsByMusicId.getOrDefault(musicData.getId(), new ArrayList<>())); // Assuming MusicData has a setAuthors(List<AuthorItem>) method
            return musicData;
        }).collect(Collectors.toList());

        MPJLambdaWrapper<MusicAlbumBind> adultBindMPJLambdaWrapper = new MPJLambdaWrapper<MusicAlbumBind>().eq(MusicAlbumBind::getAlbumId, id)
                .select(MusicAuthor::getId, MusicAuthor::getName, MusicAuthor::getCover)
                .leftJoin(MusicAuthor.class, MusicAuthor::getId, MusicAlbumBind::getAuthorId);
        List<AuthorItem> authorItems = musicAlbumBindMapper.selectJoinList(AuthorItem.class, adultBindMPJLambdaWrapper);


        AlbumDetailDTO albumDetailDTO = new AlbumDetailDTO();
        BeanUtils.copyProperties(musicAlbum, albumDetailDTO);
        albumDetailDTO.setTotal(list.size());
        albumDetailDTO.setMusicItems(list);
        albumDetailDTO.setAuthorItems(authorItems);
        albumDetailDTO.setSeconds(list.stream().mapToInt(MusicData::getSeconds).sum());

        return albumDetailDTO;
    }

    public AlbumDetailDTO getLoveDetails() {
        MusicAlbum musicAlbum = new MusicAlbum();
        musicAlbum.setName("已点赞歌曲");
        musicAlbum.setId(-1);

        Music music = new Music();
        music.setLove(2);
        List<MusicData> list = getMusicDataList(music);

        AlbumDetailDTO albumDetailDTO = new AlbumDetailDTO();
        BeanUtils.copyProperties(musicAlbum, albumDetailDTO);
        albumDetailDTO.setTotal(list.size());
        albumDetailDTO.setMusicItems(list);
        albumDetailDTO.setSeconds(list.stream().mapToInt(MusicData::getSeconds).sum());
        return albumDetailDTO;
    }

    public AlbumDetailDTO getRandomDetails() {
        MusicAlbum musicAlbum = new MusicAlbum();
        musicAlbum.setName("每日随机");
        musicAlbum.setId(-3);

        List<Integer> ids = musicService.getRandomIds();

        Music music = new Music();
        music.setIds(ids);
        List<MusicData> list = getMusicDataList(music);

        AlbumDetailDTO albumDetailDTO = new AlbumDetailDTO();
        BeanUtils.copyProperties(musicAlbum, albumDetailDTO);
        albumDetailDTO.setTotal(list.size());
        albumDetailDTO.setMusicItems(list);
        albumDetailDTO.setSeconds(list.stream().mapToInt(MusicData::getSeconds).sum());
        return albumDetailDTO;
    }

    public List<MusicData> getMusicDataList(Music music) {
        MPJLambdaWrapper<MusicBind> queryWrapper = new MPJLambdaWrapper<MusicBind>()
                .eq(music.getLove() != null, Music::getLove, music.getLove())
                .in(music.getIds().isEmpty(), Music::getId, music.getIds())
                .select(Music::getId, Music::getFilesId, Music::getTitle, Music::getYear, Music::getLove, Music::getListen, Music::getCover, Music::getSeconds)
                .selectAs(MusicAlbum::getId, MusicData::getAlbumId)
                .selectAs(MusicAlbum::getName, MusicData::getAlbumName)
                .leftJoin(Music.class, Music::getId, MusicBind::getMusicId)
                .leftJoin(MusicAlbum.class, MusicAlbum::getId, MusicBind::getAlbumId);

        List<MusicData> list = musicBindMapper.selectJoinList(MusicData.class, queryWrapper);
        if (list.isEmpty()) return list;

        List<Integer> ids = list.stream().map(MusicData::getId).collect(Collectors.toList());
        MPJLambdaWrapper<MusicAuthorBind> authorBindMPJLambdaWrapper = new MPJLambdaWrapper<MusicAuthorBind>().in(MusicAuthorBind::getMusicId, ids)
                .select(MusicAuthor::getId, MusicAuthor::getName, MusicAuthor::getCover)
                .selectAs(MusicAuthorBind::getMusicId, AuthorItem::getBindId)
                .leftJoin(MusicAuthor.class, MusicAuthor::getId, MusicAuthorBind::getAuthorId);
        List<AuthorItem> musicAuthors = musicAuthorBindMapper.selectJoinList(AuthorItem.class, authorBindMPJLambdaWrapper);

        Map<Integer, List<AuthorItem>> authorsByMusicId = musicAuthors.stream()
                .collect(Collectors.groupingBy(AuthorItem::getBindId)); // Assuming AuthorItem has a getMusicId() method

        list = list.stream().map(musicData -> {
            musicData.setAuthorItems(authorsByMusicId.getOrDefault(musicData.getId(), new ArrayList<>())); // Assuming MusicData has a setAuthors(List<AuthorItem>) method
            return musicData;
        }).collect(Collectors.toList());

        return list;
    }
}
