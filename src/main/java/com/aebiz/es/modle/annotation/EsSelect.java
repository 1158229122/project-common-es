package com.aebiz.es.modle.annotation;

import com.aebiz.es.modle.enums.EsSelectType;

import java.lang.annotation.*;

/**
 * @author jim
 * @date 2022/6/29 11:36
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface EsSelect {
    /**
     * 查询语句
     *
     * @return
     */
    String value() default "";

    /**
     * 查询类型
     *
     * @return
     */
    EsSelectType selectType() default EsSelectType.SQL;
}
