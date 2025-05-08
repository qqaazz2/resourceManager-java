package com.example.resourcemanager.service.music;

import com.example.resourcemanager.entity.music.MusicAlbumBind;
import com.example.resourcemanager.entity.music.MusicBind;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MusicBindService {
    List<MusicBind> createData(List<MusicBind> list);
}
