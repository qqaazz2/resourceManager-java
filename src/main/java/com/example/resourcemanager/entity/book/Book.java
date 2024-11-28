package com.example.resourcemanager.entity.book;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.annotations.Update;

@Data
public class Book {
    @NotNull(groups = {Update.class},message = "书籍ID不能为空")
    @TableId(type = IdType.AUTO)
    Integer id;

    Integer filesId;

    Integer coverId;

    @NotNull(groups = {Update.class},message = "书籍名称不能为空")
    String name;

    String author;

    String profile;

    Integer status;

    Double progress;

    Integer deleted;

    String publishing;

    Integer readTagNum;

    @TableField(exist = false)
    String hash;

    @TableField(exist = false)
    Integer parentId;
}
