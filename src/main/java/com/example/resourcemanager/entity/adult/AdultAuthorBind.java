package com.example.resourcemanager.entity.adult;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class AdultAuthorBind {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer adultId;
    private Integer adultAuthorId;
    private Short deleted;
}
