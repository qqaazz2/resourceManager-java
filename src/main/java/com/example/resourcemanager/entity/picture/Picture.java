package com.example.resourcemanager.entity.picture;

import lombok.Data;

import java.util.Date;

@Data
public class Picture {
    private Integer id;
    private Integer filesId;
    private Integer love;
    private Integer show;
    private String author;
    private Integer width;
    private Integer height;
    private Float mp;
    private Date createTime;
}
