package com.example.resourcemanager.entity.music;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
public class MusicList {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    @TableField(fill = FieldFill.INSERT)
    private String createTime;
    @TableLogic
    private Short deleted;
}
