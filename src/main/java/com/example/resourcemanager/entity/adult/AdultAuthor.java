package com.example.resourcemanager.entity.adult;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class AdultAuthor {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Date date;
    private String avatar;
    private String biography;
    private Short deleted;
}
