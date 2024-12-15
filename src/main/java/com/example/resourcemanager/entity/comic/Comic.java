package com.example.resourcemanager.entity.comic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.resourcemanager.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class Comic{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer filesId;
    private Date readTime;
    private Integer status;
    private String anchor;
    private Integer total;
    private Integer number;
    private Integer cover;
    private String note;

    @TableField(exist = false)
    String hash;

    @TableField(exist = false)
    Integer parentId;
}
