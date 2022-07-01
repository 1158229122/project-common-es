package com.aebiz.es.common.proxy;

import com.aebiz.es.common.exector.ProxyHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Proxy;

/**
 * @author jim
 * @date 2022/6/28 15:14
 */
public class EsBeanProxyFactory<T> implements ApplicationContextAware, FactoryBean<T> {

    private Class<T> targetInterfaceClazz;
    private boolean visitQueryBeanParent = Boolean.TRUE;
    private ApplicationContext applicationContext;

    public EsBeanProxyFactory(Class<T> targetInterfaceClazz) {
        this.targetInterfaceClazz = targetInterfaceClazz;
    }


    @Override
    public T getObject() throws Exception {
        ProxyHandler executeHandler = applicationContext.getBean(ProxyHandler.class);
        return  (T) Proxy.newProxyInstance(
                targetInterfaceClazz.getClassLoader(),
                new Class[]{targetInterfaceClazz},
                new EsBaseRepositoryProxy(targetInterfaceClazz,executeHandler)
        );
    }

    @Override
    public Class<?> getObjectType() {
        return targetInterfaceClazz;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
