package com.example.resourcemanager.entity.book;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Data
public class Series {
    @TableId(type = IdType.AUTO)
    @NotNull(groups = {Update.class},message = "系列ID不能为空")
    Integer id;

    Integer filesId;

    @NotNull(groups = {Update.class},message = "系列名称不能为空")
    String name;

    String author;

    Integer overStatus;

    Integer status;

    Integer deleted;

    Integer love;

    String profile;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    Date lastReadTime;

    Integer num;

    Integer coverId;

    Integer isChild;
}
