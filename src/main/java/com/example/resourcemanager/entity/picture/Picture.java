package com.example.resourcemanager.entity.picture;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class Picture {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer filesId;
    private Integer love;
    private Integer display;
    private String author;
    private Integer width;
    private Integer height;
    private Float mp;
    private Date createTime;
}
