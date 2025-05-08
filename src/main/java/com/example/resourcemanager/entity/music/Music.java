package com.example.resourcemanager.entity.music;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.example.resourcemanager.entity.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Music{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer filesId;
    private String title;
    private Integer year;
    private String genre;
    private Integer track;
    private Integer love;
    private Integer listen;
    private String disc;
    private String composer;
    private String language;
    private String cover;
    private Integer seconds;

    @TableLogic
    private Short deleted;

    @TableField(exist = false)
    private String filePath;

    @TableField(exist = false)
    private String album;

    @TableField(exist = false)
    private List<String> authors;

    @TableField(exist = false)
    private List<Integer> ids;
}
