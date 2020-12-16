package com.lemon.blog.dao.handler;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CreateAndUpdateTimeFillHandler implements MetaObjectHandler {

    public static final String CREATED_AT_FIELD_NAME = "createdAt";

    public static final String UPDATED_AT_FIELD_NAME = "updatedAt";

    @Override
    public void insertFill(MetaObject metaObject) {
        autoFillIfNotSet(metaObject, CREATED_AT_FIELD_NAME, LocalDateTime.class, LocalDateTime.now(), FieldFill.INSERT);
        autoFillIfNotSet(metaObject, UPDATED_AT_FIELD_NAME, LocalDateTime.class, LocalDateTime.now(), FieldFill.INSERT);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        autoFillIfNotSet(metaObject, UPDATED_AT_FIELD_NAME, LocalDateTime.class, LocalDateTime.now(), FieldFill.UPDATE);
    }

    private <T> void autoFillIfNotSet(MetaObject metaObject, String fieldName, Class<T> fieldClass, T fieldValue, FieldFill fieldFill) {
        if (metaObject.hasSetter(fieldName) && getFieldValByName(fieldName, metaObject) == null) {
            switch (fieldFill) {
                case INSERT:
                    this.strictInsertFill(metaObject, fieldName, fieldClass, fieldValue);
                    break;
                case UPDATE:
                    this.strictUpdateFill(metaObject, fieldName, fieldClass, fieldValue);
                    break;
                case INSERT_UPDATE:
                    this.strictInsertFill(metaObject, fieldName, fieldClass, fieldValue);
                    this.strictUpdateFill(metaObject, fieldName, fieldClass, fieldValue);
                    break;
            }


        }
    }

}