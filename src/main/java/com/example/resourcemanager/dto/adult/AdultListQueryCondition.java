package com.example.resourcemanager.dto.adult;

import com.example.resourcemanager.dto.QueryCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdultListQueryCondition extends QueryCondition {
    Integer download;
    String name;
    String number;
    String produce;
    List<Integer> tagIds = new ArrayList<>();
    List<Integer> authorIds = new ArrayList<>();

    public AdultListQueryCondition(Integer page) {
        super(page);
    }
}
