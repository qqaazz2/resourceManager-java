package com.example.resourcemanager.entity.adult;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

import java.util.Date;

@Data
public class Adult {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String number;
    private String synopsis;
    private Date date;
    private Short minutes;
    private String bt_url;
    private String url;
    private String produce;
    private Integer embyId;
    @TableLogic
    private Short deleted;
    private String series;
    private Short download;
}
