package com.example.resourcemanager.service.adult;

import com.example.resourcemanager.dto.PageVO;
import com.example.resourcemanager.dto.adult.AdultDetailDTO;
import com.example.resourcemanager.dto.adult.AdultListDTO;
import com.example.resourcemanager.dto.adult.AdultListQueryCondition;
import com.example.resourcemanager.entity.adult.Adult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface AdultService {
    public PageVO<AdultListDTO> getList(AdultListQueryCondition condition);

    String[] getImages(String number);

    AdultDetailDTO randData();

    AdultDetailDTO getOne(Integer id);

    Integer addData(AdultDetailDTO adult);

    Integer editData(AdultDetailDTO adult);

    void delImg(String path);

    void bindEmbyId(Integer id,Integer embyId);
}
