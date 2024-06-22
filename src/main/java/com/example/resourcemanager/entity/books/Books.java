package com.example.resourcemanager.entity.books;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class Books {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Date last_read;
    private Date edit_time;
    private String cover;
    private String author;
    private String illustrator;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date add_time;

    @TableField(exist = false)
    private Integer read_num;
    @TableField(exist = false)
    private Integer count = 0;
}
