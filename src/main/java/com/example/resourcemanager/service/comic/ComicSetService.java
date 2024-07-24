package com.example.resourcemanager.service.comic;

import com.example.resourcemanager.entity.Files;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ComicSetService {
    List<Files> createSave(List<Files> files);
}
