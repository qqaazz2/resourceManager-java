package com.example.resourcemanager.dto.comic;

import com.example.resourcemanager.dto.QueryCondition;

public class ComicSetListQueryCondition extends QueryCondition {

    private Integer id;
    private Integer status;
    private Integer love;
    private Integer readStatus;
    private String name;

    public ComicSetListQueryCondition(Integer page) {
        super(page);
    }

    public Integer getLove() {
        return love;
    }

    public void setLove(Integer love) {
        this.love = love;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
