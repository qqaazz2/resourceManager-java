package com.example.resourcemanager.entity.music;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class MusicBind {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer albumId;
    private Integer musicId;
    @TableLogic
    private Short deleted;
}
