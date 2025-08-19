package com.example.resourcemanager.service.music;

import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.logs.music.MusicData;
import com.example.resourcemanager.dto.logs.music.MusicListQueryCondition;
import org.springframework.stereotype.Service;

@Service
public interface MusicInfoService {
    PageVO<MusicData> getMusicInfoList(MusicListQueryCondition queryCondition);
}
