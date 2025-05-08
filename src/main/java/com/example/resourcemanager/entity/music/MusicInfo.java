package com.example.resourcemanager.entity.music;

import lombok.Data;

@Data
public class MusicInfo {
    private Integer id;
    private Integer filesId;
    private String cover;
    private Integer seconds;
    private Short love;
    private Integer year;
    private String title;
    private Integer albumId;
    private String albumName;
    private Short listen;
    private String url;
}
