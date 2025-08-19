package com.example.resourcemanager.service.music;

import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.logs.music.*;
import com.example.resourcemanager.entity.music.MusicAuthor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MusicAuthorService {
    List<MusicAuthor> getList();

    List<MusicAuthor> createData(List<MusicAuthor> list);

    PageVO<MusicAuthor> getList(MusicAuthorListQueryCondition queryCondition);

    AuthorDetailDTO getDetails(Integer id);
}
