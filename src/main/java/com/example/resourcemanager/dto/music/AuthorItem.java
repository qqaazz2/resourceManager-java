package com.example.resourcemanager.dto.music;

import lombok.Data;

@Data
public class AuthorItem {
    Integer id;
    String name;
    String cover;

    Integer bindId;
}
