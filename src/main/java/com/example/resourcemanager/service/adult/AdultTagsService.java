package com.example.resourcemanager.service.adult;

import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.adult.AdultDetailDTO;
import com.example.resourcemanager.dto.adult.AdultListDTO;
import com.example.resourcemanager.dto.adult.AdultListQueryCondition;
import com.example.resourcemanager.dto.adult.AdultTagDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdultTagsService {
    List<Integer> getTagIds(List<AdultTagDTO> tagDTOList);

    List<AdultTagDTO> getList();

    void delData(Integer id);
}
