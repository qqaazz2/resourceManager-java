package com.example.resourcemanager.dto.logs;

import lombok.Data;

@Data
public class LogDTO {
    Integer id;
    String time;
    String message;
    String level;
    String callerClass;

    public LogDTO(Integer id,String time,String message,String level,String callerClass){
        this.id = id;
        this.time = time;
        this.message = message;
        this.level = level;
        this.callerClass = callerClass;
    }

    public LogDTO(){}
}
