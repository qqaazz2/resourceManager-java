package com.example.resourcemanager.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

public class BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(fill = FieldFill.INSERT)
    private Date add_time;
    private Date edit_time;

    @TableLogic
    private Integer deleted;
    private Integer status;
}
