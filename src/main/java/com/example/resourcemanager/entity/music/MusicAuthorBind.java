package com.example.resourcemanager.entity.music;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class MusicAuthorBind {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer musicId;
    private Integer authorId;
    @TableLogic
    private Short deleted;
}
