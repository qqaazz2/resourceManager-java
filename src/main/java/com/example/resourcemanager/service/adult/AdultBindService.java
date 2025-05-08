package com.example.resourcemanager.service.adult;

import com.example.resourcemanager.dto.adult.AdultTagDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdultBindService {
    void saveData(List<AdultTagDTO> list, Integer adultId, Boolean isEdit);
}
