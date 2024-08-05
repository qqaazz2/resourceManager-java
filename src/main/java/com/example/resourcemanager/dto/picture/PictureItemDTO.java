package com.example.resourcemanager.dto.picture;

import lombok.Data;

@Data
public class PictureItemDTO {
    private Integer id;
    private String modifiableName;
    private String fileName;
    private String filePath;
    private String cover;
    private Integer pictureId;
    private Integer love;
}
