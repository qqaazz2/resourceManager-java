package com.example.resourcemanager.entity.music;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class MusicAuthor {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String cover;
    private String profile;
    @TableLogic
    private Short deleted;
}
