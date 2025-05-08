package com.example.resourcemanager.dto.adult;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

@Data
public class AdultDetailDTO {
    @NotNull(groups = {Update.class}, message = "影片ID不可为空")
    Integer id;
    @NotBlank(groups = {Update.class, Insert.class}, message = "影片名称不可为空")
    String name;
    @NotBlank(groups = {Update.class, Insert.class}, message = "影片编号不可为空")
    String number;
    String synopsis;
    @NotNull(groups = {Update.class, Insert.class}, message = "发售时间不可为空")
    Date date;
    @NotNull(groups = {Update.class, Insert.class}, message = "播放时长不可为空")
    Short minutes;
    String btUrl;
    String url;
    String produce;
    String series;
    Short download;
    String[] images;
    String cover;
    Integer embyId;

    List<AdultAuthorDTO> authorList;
    List<AdultTagDTO> tagList;
}
