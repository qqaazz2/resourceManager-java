package com.example.resourcemanager.service.comic;

import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.comic.ComicSetDetailDTO;
import com.example.resourcemanager.dto.comic.ComicSetListDTO;
import com.example.resourcemanager.dto.comic.ComicSetListQueryCondition;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.comic.ComicSet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ComicSetService {
    List<ComicSet> createSave(List<ComicSet> files);

    PageVO<ComicSetListDTO> getList(ComicSetListQueryCondition queryCondition);

    void setLove(Integer id,Integer love);

    ComicSetDetailDTO getDetail(Integer id);

    ComicSetDetailDTO updateData(ComicSetDetailDTO comicSet);
}
