package com.example.resourcemanager.service.comic;

import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.comic.ComicListDTO;
import com.example.resourcemanager.dto.comic.ComicSetListQueryCondition;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.comic.Comic;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public interface ComicService {
    PageVO<ComicListDTO> getList(ComicSetListQueryCondition queryCondition);

    void addComic(Map<String, Files> map,Integer comicSetID);

    List<Comic> createSave(List<Comic> comicList);

    List<String> getPageList(String path);

    Boolean updateNumber(Integer id, Integer num, Boolean over, Integer filesId);
}
