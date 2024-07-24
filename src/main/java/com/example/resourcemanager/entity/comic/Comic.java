package com.example.resourcemanager.entity.comic;

import com.example.resourcemanager.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class Comic extends BaseEntity {
    private Integer filesId;
    private Date readTime;
    private String anchor;
    private Integer total;
    private Integer number;
    private String cover;
}
