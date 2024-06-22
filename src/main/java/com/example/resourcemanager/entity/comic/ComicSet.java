package com.example.resourcemanager.entity.comic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.resourcemanager.entity.BaseEntity;
import lombok.Data;

@Data
public class ComicSet extends BaseEntity {
    private String name;
    private String cover;
    private String note;
    private String press;
    private String language;
}
