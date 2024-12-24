package com.example.resourcemanager.service;

import com.example.resourcemanager.dto.setting.ProportionDTO;
import com.example.resourcemanager.dto.setting.TimeCountDTO;
import com.example.resourcemanager.entity.Files;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface FilesService {

    List<Files> getByType(Integer type);

    List<Files> renameFiles(List<Files> files);

    List<Files> createFiles(List<Files> files);

    void removerFiles(List<Files> files);

    void rename(Integer id,String name);

    Files getFiles(Files files);

    Files createFile(Files files);

    List<ProportionDTO> filesProportion();

    Map<String,Object> getFilesCount();

    List<TimeCountDTO> getYearMonth();
}
