package com.example.resourcemanager.entity.music;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class MusicAlbum {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String cover;
    private String profile;
    private Integer year;

    @TableLogic
    private Short deleted;
}
