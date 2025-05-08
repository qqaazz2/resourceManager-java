package com.example.resourcemanager.service.music;

import com.example.resourcemanager.entity.music.MusicAuthor;
import com.example.resourcemanager.entity.music.MusicAuthorBind;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MusicAuthorBindService {
    List<MusicAuthorBind> createData(List<MusicAuthorBind> list);
}
