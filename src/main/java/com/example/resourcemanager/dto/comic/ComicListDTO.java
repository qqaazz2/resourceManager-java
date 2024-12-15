package com.example.resourcemanager.dto.comic;

import lombok.Data;

import java.util.Date;

@Data
public class ComicListDTO {
    private Integer id;
    private Integer filesId;
    private Date readTime;
    private Integer status;
    private String name;
    private Integer total;
    private Integer number;
    private String filePath;
    private String coverPath;
}
