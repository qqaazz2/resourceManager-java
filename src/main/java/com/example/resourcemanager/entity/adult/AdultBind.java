package com.example.resourcemanager.entity.adult;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class AdultBind {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer adultId;
    private Integer adultTagsId;
    private Short deleted;

    public AdultBind(){}

    public AdultBind(Integer tagId,Integer adultId){
        this.adultId = adultId;
        this.adultTagsId = tagId;
    }
}
