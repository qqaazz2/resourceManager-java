package com.example.resourcemanager.dto.comic;

import lombok.Data;

@Data
public class ComicSetListDTO {
    private Integer id;
    private Integer filesId;
    private String name;
    private Integer status;
    private String coverPath;
    private Integer love;
    private Integer readStatus;
}
