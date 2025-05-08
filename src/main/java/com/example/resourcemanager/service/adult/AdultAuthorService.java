package com.example.resourcemanager.service.adult;

import com.example.resourcemanager.dto.adult.AdultAuthorDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdultAuthorService {
    List<AdultAuthorDTO> getList();

    void addData(AdultAuthorDTO authorDTO);
    void editData(AdultAuthorDTO authorDTO);
    void delData(Integer id);
}
