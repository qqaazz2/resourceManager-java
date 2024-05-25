package com.example.resourcemanager.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;

public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("add_time", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
