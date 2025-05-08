package com.example.resourcemanager.dto.adult;

import lombok.Data;

import java.util.Date;

@Data
public class AdultListDTO {
    private Integer id;
    private String name;
    private String number;
    private Date date;
    private Short minutes;
    private String produce;
    private Short download;
    private String img;
}
