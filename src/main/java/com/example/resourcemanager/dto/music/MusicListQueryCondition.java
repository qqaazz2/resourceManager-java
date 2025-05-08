package com.example.resourcemanager.dto.music;

import com.example.resourcemanager.dto.QueryCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.annotations.Update;

@Data
@EqualsAndHashCode(callSuper = true)
public class MusicListQueryCondition extends QueryCondition {
    private Integer love;
    private Integer author;
    private Integer album;
    private boolean all = false;
}
