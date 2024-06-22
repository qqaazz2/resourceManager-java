package com.example.resourcemanager.entity.comic;

import lombok.Data;

import java.util.Date;

@Data
public class Comic {
    private Integer ComicSetId;
    private String name;
    private Date readTime;
    private String anchor;
    private Integer total;
    private String url;
    private Float size;
    private String cover;
}
