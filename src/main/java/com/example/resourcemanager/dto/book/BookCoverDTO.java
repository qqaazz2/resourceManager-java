package com.example.resourcemanager.dto.book;

import lombok.Data;

@Data
public class BookCoverDTO {
    private Integer id;
    private String name;
    private Integer coverId;
    private String coverPath;
}
