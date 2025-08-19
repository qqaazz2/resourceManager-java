package com.example.resourcemanager.dto.logs.music;

import com.example.resourcemanager.dto.QueryCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class LogListQueryCondition extends QueryCondition {
    private List<String> levels;
}
