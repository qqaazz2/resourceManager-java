package com.example.resourcemanager.dto.picture;

import com.example.resourcemanager.dto.QueryCondition;

public class PictureQueryCondition extends QueryCondition {
    private Integer love;
    private Integer display;
    private String author;
    private Integer picture_id = -1;

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(Integer picture_id) {
        this.picture_id = picture_id;
    }
}
