package com.example.resourcemanager.service;

import com.example.resourcemanager.entity.Files;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FilesService {

    List<Files> getByType(Integer type);

    List<Files> renameFiles(List<Files> files);

    List<Files> createFiles(List<Files> files);

    void removerFiles(List<Files> files);
}
