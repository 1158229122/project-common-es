package com.aebiz.es.common.exector.impl;

import com.aebiz.es.common.exector.ProxyHandler;
import com.aebiz.es.common.exector.factory.EsProxyExecuteFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * es 代理的执行器
 * @author jim
 * @date 2022/6/28 15:31
 */
@Component
@AllArgsConstructor
public class ProxyHandlerImpl implements ProxyHandler {
    private final EsProxyExecuteFactory handlerFactory;
    //es 真正代理执行的方法
    public Object exec(Object proxy, Method method, Object[] args) {
        return handlerFactory.match(method.getDeclaringClass()).handler(proxy, method, args);
    }
}
