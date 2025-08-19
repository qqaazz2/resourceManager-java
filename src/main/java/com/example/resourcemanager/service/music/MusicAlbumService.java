package com.example.resourcemanager.service.music;

import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.logs.music.AlbumDetailDTO;
import com.example.resourcemanager.dto.logs.music.MusicAlbumListQueryCondition;
import com.example.resourcemanager.entity.music.MusicAlbum;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MusicAlbumService {

    List<MusicAlbum> getList();

    List<MusicAlbum> createData(List<MusicAlbum> list);

    PageVO<MusicAlbum> getAlbumList(MusicAlbumListQueryCondition condition);

    AlbumDetailDTO getAlbumDetail(Integer id);
}
