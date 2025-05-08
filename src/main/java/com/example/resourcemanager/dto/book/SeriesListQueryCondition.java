package com.example.resourcemanager.dto.book;

import com.example.resourcemanager.dto.QueryCondition;
import com.example.resourcemanager.dto.book.group.SpecificCheck;
import jakarta.validation.constraints.NotNull;

public class SeriesListQueryCondition extends QueryCondition {
    @NotNull(groups = {SpecificCheck.class}, message = "ID不可为空")
    private Integer id;
    @NotNull(groups = {SpecificCheck.class}, message = "喜欢状态不可为空")
    private Integer love;
    private Integer status;
    private String name;

    private Integer overStatus;

    public SeriesListQueryCondition(Integer page) {
        super(page);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLove(Integer love) {
        this.love = love;
    }


    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLove() {
        return love;
    }


    public Integer getStatus() {
        return status;
    }

    public Integer getOverStatus() {
        return overStatus;
    }

    public void setOverStatus(Integer overStatus) {
        this.overStatus = overStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

