package com.aebiz.es.modle.annotation;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface EsMetaData {
    /**
     * 索引名称
     * @return
     */
    String indexName();

    /**
     * 是否自动创建索引
     * @return
     */
    boolean createIndex() default true;
}