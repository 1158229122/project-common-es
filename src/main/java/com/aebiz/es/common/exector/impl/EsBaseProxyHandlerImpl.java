package com.aebiz.es.common.exector.impl;

import com.aebiz.es.common.exector.EsProxyHandler;
import com.aebiz.es.common.metadata.EsMetadata;
import com.aebiz.es.common.proxy.EsBaseRepositoryProxy;
import com.aebiz.es.common.template.EsTemplate;
import com.aebiz.es.modle.EsBaseEntity;
import com.aebiz.es.modle.support.BaseCurdRepository;
import com.aebiz.es.modle.support.BaseEsRepository;
import lombok.AllArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * es 系统自带方法
 * @author jim
 * @date 2022/6/28 17:45
 */
@Component
@AllArgsConstructor
public class EsBaseProxyHandlerImpl implements EsProxyHandler {
    private final EsTemplate esTemplate;
    /**
     * 核心业务处理
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    @Override
    public Object handler(Object proxy, Method method, Object[] args) {
        EsBaseRepositoryProxy repositoryProxy = (EsBaseRepositoryProxy) proxy;
        switch (method.getName()){
            case "create":
                return esTemplate.create((EsBaseEntity) args[0]);
            case "batchCreate":
                List<EsBaseEntity> list = (List<EsBaseEntity>) args[0];
                return esTemplate.batchCreate((List<EsBaseEntity>) args[0]);
            case "update":
                return esTemplate.update((EsBaseEntity) args[0]);
            case "batchUpdate":
                return esTemplate.batchUpdate((List<EsBaseEntity>) args[0]);
            case "findById":
                return esTemplate.findById((Serializable) args[0],this.getBaseEntity(repositoryProxy.getTargetInterface()));
            case "deleteById":
                return esTemplate.deleteById((Serializable) args[0],this.getBaseEntity(repositoryProxy.getTargetInterface()));
            case "refresh":
                return esTemplate.refresh(this.getBaseEntity(repositoryProxy.getTargetInterface()));
            case "count":
                return esTemplate.count((QueryBuilder) args[0],this.getBaseEntity(repositoryProxy.getTargetInterface()));
            case "queryPage":
                return esTemplate.queryPage((SearchSourceBuilder) args[0],this.getBaseEntity(repositoryProxy.getTargetInterface()));

        }

        return null;
    }

    private Class<? extends EsBaseEntity> getBaseEntity(Class interfaceClass){
        Class<? extends EsBaseEntity> baseEntity =  EsMetadata.getBaseEntity(interfaceClass);
        if (Objects.isNull(baseEntity)){
            throw new InvalidStateException("泛型没有找到。你是不是没有指定泛型啊");
        }
        return baseEntity;
    }


    /**
     * 匹配查找对应的es执行器
     *
     * @param clazz
     * @return
     */
    @Override
    public boolean matching(Class clazz) {
        return Objects.equals(clazz,BaseCurdRepository.class)||Objects.equals(BaseEsRepository.class,clazz);
    }
}
