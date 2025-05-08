package com.example.resourcemanager.dto.adult;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Data
public class AdultAuthorDTO{

    @NotNull(groups = {Update.class})
    Integer id;
    @NotBlank(groups = {Update.class, Insert.class})
    String name;
    String avatar;
    @NotNull(groups = {Update.class, Insert.class})
    Date date;
    String biography;
}