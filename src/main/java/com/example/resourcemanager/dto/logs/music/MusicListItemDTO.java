package com.example.resourcemanager.dto.logs.music;

import lombok.Data;

@Data
public class MusicListItemDTO {
    private Integer filesId;
    private String fileName;
    private String cover;
    private String title;
    private String author;
    private Integer id;
    private Integer love;
}
