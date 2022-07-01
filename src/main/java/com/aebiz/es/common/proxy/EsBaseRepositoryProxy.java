package com.aebiz.es.common.proxy;

import com.aebiz.es.common.exector.ProxyHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author jim
 * @date 2022/6/28 15:20
 */
public class EsBaseRepositoryProxy<T>  implements InvocationHandler {
    private Class<T> targetInterface;

    private ProxyHandler executeHandler;

    public EsBaseRepositoryProxy(Class<T> targetInterface, ProxyHandler executeHandler) {
        this.targetInterface = targetInterface;
        this.executeHandler = executeHandler;
    }

    public Class<T> getTargetInterface() {
        return targetInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return executeHandler.exec(this,method,args);
    }
}
