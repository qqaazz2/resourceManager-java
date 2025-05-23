package com.example.resourcemanager.dto.music;

import lombok.Data;

@Data
public class AuthorDetailDTO {
    private Integer id;
    private String name;
    private String cover;
    private String profile;
    private Integer loveNum;
}
