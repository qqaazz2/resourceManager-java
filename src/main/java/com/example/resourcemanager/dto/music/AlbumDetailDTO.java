package com.example.resourcemanager.dto.music;

import lombok.Data;

import java.util.List;

@Data
public class AlbumDetailDTO {
    Integer id;
    String name;
    String cover;
    Integer seconds;
    Integer year;
    Integer total;
    List<AuthorItem> authorItems;
    List<MusicData> musicItems;
    String url;
}
