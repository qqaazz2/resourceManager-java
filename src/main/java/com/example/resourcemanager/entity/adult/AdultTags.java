package com.example.resourcemanager.entity.adult;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class AdultTags {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String synopsis;
    private Short deleted;
}
