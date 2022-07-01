package com.aebiz.es.common.exector;

import com.aebiz.es.modle.domain.RequestMeta;

import java.lang.reflect.Method;

/**
 * @author jim
 * @date 2022/6/29 17:35
 */
public interface EsQueryProxyHandler extends MatchingQuery {
    /**
     * 处理
      * @param requestMeta
     * @param method
     * @param args
     * @return
     */
    Object handle(RequestMeta requestMeta, Method method, Object[] args);

}
