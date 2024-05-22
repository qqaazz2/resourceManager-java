package com.example.resourcemanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class BooksDetails {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer books_id;
    private Date read_time;
    private Integer sort;
    private Integer status;
    private Date add_time;
    private Date edit_time;
    private String cover;
    private String name;
}
