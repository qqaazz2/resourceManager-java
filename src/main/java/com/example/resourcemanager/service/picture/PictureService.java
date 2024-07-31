package com.example.resourcemanager.service.picture;

import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.picture.PictureDetailDTO;
import com.example.resourcemanager.dto.picture.PictureItemDTO;
import com.example.resourcemanager.dto.picture.PictureQueryCondition;
import com.example.resourcemanager.entity.Files;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PictureService {
    List<Files> createData(List<Files> files);

    PageVO<PictureDetailDTO> getFolderList(PictureQueryCondition queryCondition);

    List<PictureDetailDTO> getRandList(Integer limit);

    void setDisplay(Integer id,Integer display);

    void setLove(Integer id,Integer love);
}
