package com.aebiz.es.common.param;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 参数替换
 * @author jim
 * @date 2022/6/30 11:38
 */
public interface ParamHandler {

    /**
     * 字符串处理替换
     * @param originStr
     * @param method
     * @param args
     * @return
     */
    String handle(String originStr, Method method,Object[] args);
}
