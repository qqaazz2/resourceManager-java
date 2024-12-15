package com.example.resourcemanager.entity.comic;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.resourcemanager.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Data
public class ComicSet {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer filesId;
    private String name;
    private String author;
    private Integer status;
    private Integer cover;
    private Integer love;
    private String note;
    private String press;
    private String language;
    private Date lastReadTime;
    private Integer deleted;
    private Integer readStatus;
}
