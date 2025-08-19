package com.example.resourcemanager.service.impl.music;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.logs.music.MusicData;
import com.example.resourcemanager.dto.logs.music.MusicListItemDTO;
import com.example.resourcemanager.dto.logs.music.MusicListQueryCondition;
import com.example.resourcemanager.entity.music.*;
import com.example.resourcemanager.mapper.music.MusicMapper;
import com.example.resourcemanager.service.music.MusicService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {
    @Resource
    MusicMapper musicMapper;

    @Resource
    RedisTemplate redisTemplate;

    private static final String MUSIC_KEY_PREFIX = "music:randomId";
    private static final ZoneId SHANGHAI_ZONE = ZoneId.of("Asia/Shanghai"); // 显式指定时区
    private static final int RANDOM_COUNT = 50; // 显式指定时区

    @Override
    public List<Music> createData(List<Music> musicList) {
        boolean isTrue = this.saveBatch(musicList);
        if (!isTrue) throw new BizException("4000", "创建音乐数据失败");
        return musicList;
    }

    @Override
    public PageVO<MusicListItemDTO> getMusicList(MusicListQueryCondition musicQueryCondition) {
        List<MusicListItemDTO> list = musicMapper.getList(musicQueryCondition);
        Integer count = musicMapper.count(musicQueryCondition);
        return new PageVO(musicQueryCondition.getLimit(), musicQueryCondition.getPage(), count, list);
    }

    @Override
    public List<MusicData> getRandom() {
        List<Integer> ids = getRandomIds();

        LambdaQueryWrapper<Music> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Music::getId, ids);
        lambdaQueryWrapper.select(Music::getId, Music::getTitle, Music::getCover);
        List<Music> musicList = this.list(lambdaQueryWrapper);

        List<MusicData> musicDataList = new ArrayList<>();
        musicList.forEach(item -> {
            MusicData musicData = new MusicData();
            BeanUtils.copyProperties(item, musicData);
            musicDataList.add(musicData);
        });
        return musicDataList;
    }

    public List<Integer> getRandomIds() {
        List<Integer> ids;
        if (!redisTemplate.hasKey(MUSIC_KEY_PREFIX)) {
            LambdaQueryWrapper<Music> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.select(Music::getId);
            List<Music> list = this.list(lambdaQueryWrapper);
            ids = list.stream().map(Music::getId).collect(Collectors.toList());


            if (RANDOM_COUNT >= ids.size()) {
                Collections.shuffle(ids);
            } else {
                Collections.shuffle(ids);
                ids = ids.subList(0, RANDOM_COUNT);
            }

            LocalDateTime now = LocalDateTime.now(SHANGHAI_ZONE);
            LocalDateTime end = now.toLocalDate().plusDays(1).atStartOfDay();
            long seconds = Duration.between(now, end).getSeconds();

            redisTemplate.opsForList().rightPushAll(MUSIC_KEY_PREFIX, ids);
            redisTemplate.expire(MUSIC_KEY_PREFIX, seconds, TimeUnit.SECONDS);
        } else {
            ids = redisTemplate.opsForList().range(MUSIC_KEY_PREFIX, 0, -1);
        }
        return ids;
    }
}
