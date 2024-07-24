package com.example.resourcemanager.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.File;

@Data
public class Files extends BaseEntity {
    private String fileName;
    private int fileSize;
    private String filePath;
    private String fileType;
    private String fileNote;
    private int isFolder;
    private int type;
    private Integer parentId;
    private String modifiableName;
    private String hash;

    @TableField(exist = false)
    private File file;
    @TableField(exist = false)
    private String cover;
    @TableField(exist = false)
    private Object other;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Files files = (Files) o;

        if (!(id == files.id)) return false;
        return filePath.equals(files.filePath);
    }
}
