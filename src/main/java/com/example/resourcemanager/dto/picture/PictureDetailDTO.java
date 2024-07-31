package com.example.resourcemanager.dto.picture;

import lombok.Data;

import java.util.Date;

@Data
public class PictureDetailDTO {
    private Integer id;
    private String modifiableName;
    private String fileName;
    private String filePath;
    private Integer pictureId;
    private Integer love;
    private Integer display;
    private String author;
    private String cover;
    private Integer width;
    private Integer height;
    private Float mp;
    private Integer fileSize;
    private Date createTime;
}
