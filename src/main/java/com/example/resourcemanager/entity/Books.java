package com.example.resourcemanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class Books {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Date last_read;
    private Integer read_num;
    private Integer count;
    private Date add_time;
    private Date edit_time;
    private String cover;
    private String illustrator;
}
