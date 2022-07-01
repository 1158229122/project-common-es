package com.aebiz.es.common.exector;

import java.lang.reflect.Method;

/**
 * @author jim
 * @date 2022/6/28 15:42
 */
public interface ProxyHandler {
    Object exec(Object proxy, Method method, Object[] args);
}
