package com.example.resourcemanager.dto.logs.music;

import lombok.Data;

import java.util.List;

@Data
public class MusicData {
    Integer id;
    String title;
    Integer year;
    Short love;
    Short listen;
    String cover;
    Integer seconds;
    Integer albumId;
    String albumName;
    String url;

    List<AuthorItem> authorItems;
}
