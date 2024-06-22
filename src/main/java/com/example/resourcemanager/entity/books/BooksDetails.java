package com.example.resourcemanager.entity.books;

import com.baomidou.mybatisplus.annotation.*;
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
    @TableField(fill = FieldFill.INSERT)
    private Date add_time;
    private Date edit_time;
    private String cover;
    private String name;
    private String url;
    @TableLogic
    private Integer deleted;
    private Float progress;
}
