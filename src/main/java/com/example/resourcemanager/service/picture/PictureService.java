package com.example.resourcemanager.service.picture;

import com.example.resourcemanager.entity.Files;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PictureService {
    List<Files> createData(List<Files> files);
}
