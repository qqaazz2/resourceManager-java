package com.example.resourcemanager.dto.book;

import lombok.Data;

import java.util.Date;

@Data
public class BookListDTO {
    private Integer id;
    private String filePath;
    private Integer isFolder;
    private String name;
    private String author;
    private String profile;
    private Double progress;
    private Integer status;
    private String publishing;
    private String coverPath;
    private Integer parentId;
    private Integer coverId;
    private Integer readTagNum;
}
