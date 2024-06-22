package com.example.resourcemanager.entity.comic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class ComicContentTag {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer ComicId;
    private Integer ComicTagsId;
    @TableLogic
    private Integer deleted;
}
