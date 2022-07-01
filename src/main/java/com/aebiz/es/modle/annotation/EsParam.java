package com.aebiz.es.modle.annotation;

import java.lang.annotation.*;

/**
 * @author jim
 * @date 2022/6/30 11:22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface EsParam {
    String value();
}
