package com.aebiz.es.common.exector.impl;

import com.aebiz.es.common.exector.EsProxyHandler;
import com.aebiz.es.common.exector.factory.EsQueryProxyHandlerFactory;
import com.aebiz.es.common.metadata.EsMetadata;
import com.aebiz.es.common.proxy.EsBaseRepositoryProxy;
import com.aebiz.es.common.template.EsTemplate;
import com.aebiz.es.modle.EsBaseEntity;
import com.aebiz.es.modle.annotation.EsSelect;
import com.aebiz.es.modle.domain.RequestMeta;
import com.aebiz.es.modle.enums.EsSelectType;
import com.aebiz.es.modle.support.BaseCurdRepository;
import com.aebiz.es.modle.support.BaseEsRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author jim
 * @date 2022/6/29 14:41
 */
@Component
@AllArgsConstructor
public class EsDefinitionProxyHandlerImpl implements EsProxyHandler {

    private final EsTemplate esTemplate;
    private final EsQueryProxyHandlerFactory queryProxyExecuteHandlerFactory;

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
        Class targetInterface = repositoryProxy.getTargetInterface();
        EsSelect esSelect = method.getAnnotation(EsSelect.class);
        if (Objects.isNull(esSelect) || StringUtils.isEmpty(esSelect.value())) {
            throw new RuntimeException(targetInterface.getName()+"@EsQuery 注解不存在或参数为空");
        }
        Class<? extends EsBaseEntity> baseEntity = EsMetadata.getBaseEntity(targetInterface);
        RequestMeta requestMeta = new RequestMeta();
        requestMeta.setType(esSelect.selectType());
        requestMeta.setBaseEntity(baseEntity);
        requestMeta.setRequestJson(esSelect.value());
        requestMeta.setProxy(proxy);
        return queryProxyExecuteHandlerFactory.match( esSelect.selectType()).handle(requestMeta, method, args);
    }

    /**
     * 匹配查找对应的es执行器
     *
     * @param clazz
     * @return
     */
    @Override
    public boolean matching(Class clazz) {
        return !(Objects.equals(clazz, BaseCurdRepository.class)||Objects.equals(BaseEsRepository.class,clazz));
    }
}
