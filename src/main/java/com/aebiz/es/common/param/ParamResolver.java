package com.aebiz.es.common.param;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 参数解析
 * @author jim
 * @date 2022/6/30 11:26
 */
public interface ParamResolver {
    /**
     * 参数解析
     * @return
     */
    Map<String,Object> resolver(Method method, Object[] args);
}
