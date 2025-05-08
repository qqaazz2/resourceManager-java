package com.example.resourcemanager.service.adult;

import com.example.resourcemanager.dto.adult.AdultAuthorDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdultAuthorBindService {
    void addData(List<AdultAuthorDTO> authorDTOList, Integer adultId);
    void editData(List<AdultAuthorDTO> authorDTOList, Integer adultId);
}
