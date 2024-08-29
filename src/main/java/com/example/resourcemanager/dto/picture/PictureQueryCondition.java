package com.example.resourcemanager.dto.picture;

import com.example.resourcemanager.dto.QueryCondition;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Update;

public class PictureQueryCondition extends QueryCondition {
    private Integer love;
    private Integer display;
    private Object author;
    private Integer picture_id = -1;

    @NotNull(groups = {Update.class},message = "文件ID不可为空")
    private Integer id;

    @NotBlank(groups = {Update.class},message = "图片名称不可为空")
    private String name;

    public PictureQueryCondition(){
        super(1);
    }

    public PictureQueryCondition(Integer page) {
        super(page);
    }

    public Integer getLove() {
        return love;
    }

    public void setLove(Integer love) {
        this.love = love;
    }

    public Integer getDisplay() {
        return display;
    }

    public void setDisplay(Integer display) {
        this.display = display;
    }

    public Object getAuthor() {
        return author;
    }

    public void setAuthor(Object author) {
        this.author = author;
    }

    public Integer getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(Integer picture_id) {
        this.picture_id = picture_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
