package com.aebiz.es.common.exector;

import java.lang.reflect.Method;

/**
 * @author jim
 * @date 2022/6/28 17:36
 */
public interface EsProxyHandler extends MatchingBean {
    /**
     * 核心业务处理
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    Object handler(Object proxy, Method method, Object[] args);
}
