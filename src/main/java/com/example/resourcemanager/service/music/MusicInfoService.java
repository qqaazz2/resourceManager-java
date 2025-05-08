package com.example.resourcemanager.service.music;

import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.music.MusicData;
import com.example.resourcemanager.dto.music.MusicListQueryCondition;
import com.example.resourcemanager.entity.music.MusicBind;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MusicInfoService {
    PageVO<MusicData> getMusicInfoList(MusicListQueryCondition queryCondition);
}
