package com.example.resourcemanager.service.music;

import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.music.MusicData;
import com.example.resourcemanager.dto.music.MusicListItemDTO;
import com.example.resourcemanager.dto.music.MusicListQueryCondition;
import com.example.resourcemanager.entity.music.Music;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MusicService {
    List<Music> createData(List<Music> musicList);

    PageVO<MusicListItemDTO> getMusicList(MusicListQueryCondition musicQueryCondition);

    List<MusicData> getRandom();
}
