package com.example.resourcemanager.dto;

import lombok.Data;

@Data
public class UserInfo {
    private String name;
    private String email;
    private int mystery;

    private String oldPassWord;
    private String newPassWord;
}
