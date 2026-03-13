package com.parking.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Date;

@Configuration
public class MybatisPlusConfig implements MetaObjectHandler {
    
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
    
    @Override
    public void insertFill(MetaObject metaObject) {
        // 处理驼峰命名的字段
        this.strictInsertFill(metaObject, "gmtCreate", Date.class, new Date());
        this.strictInsertFill(metaObject, "gmtModified", Date.class, new Date());
        // 处理下划线命名的字段
        if (metaObject.hasSetter("gmt_create")) {
            this.setFieldValByName("gmt_create", new Date(), metaObject);
        }
        if (metaObject.hasSetter("gmt_modified")) {
            this.setFieldValByName("gmt_modified", new Date(), metaObject);
        }
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        // 处理驼峰命名的字段
        this.strictUpdateFill(metaObject, "gmtModified", Date.class, new Date());
        // 处理下划线命名的字段
        if (metaObject.hasSetter("gmt_modified")) {
            this.setFieldValByName("gmt_modified", new Date(), metaObject);
        }
    }
}