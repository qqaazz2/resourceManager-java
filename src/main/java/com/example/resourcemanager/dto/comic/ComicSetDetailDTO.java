package com.example.resourcemanager.dto.comic;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Data
public class ComicSetDetailDTO {
    @NotNull(groups = Update.class,message = "系列ID不可为空")
    private Integer id;
    @NotNull(groups = Update.class,message = "系列名称不能为空")
    private String name;
    private String author;
    private Integer status;
    private String note;
    private String press;
    private String language;
    private Integer love;
    private Date lastReadTime;
    private Integer readStatus;
    private Integer comicCount;
    private String coverPath;
}
