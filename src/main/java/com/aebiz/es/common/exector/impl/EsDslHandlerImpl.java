package com.aebiz.es.common.exector.impl;

import com.aebiz.es.common.exector.EsQueryProxyHandler;
import com.aebiz.es.common.exector.factory.EsResultHandlerFactory;
import com.aebiz.es.common.metadata.EsMetadata;
import com.aebiz.es.common.param.ParamHandler;
import com.aebiz.es.common.proxy.EsBaseRepositoryProxy;
import com.aebiz.es.common.util.EsTool;
import com.aebiz.es.modle.EsBaseEntity;
import com.aebiz.es.modle.annotation.EsSelect;
import com.aebiz.es.modle.domain.MetaData;
import com.aebiz.es.modle.domain.RequestMeta;
import com.aebiz.es.modle.enums.EsSelectType;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author jim
 * @date 2022/6/29 17:38
 */
@Component
@AllArgsConstructor
public class EsDslHandlerImpl implements EsQueryProxyHandler {
    private final RestHighLevelClient restEsClient;
    private final EsResultHandlerFactory resultHandlerFactory;

    private final ParamHandler paramHandler;

    /**
     * 处理
     *
     * @param requestMeta
     * @param method
     * @param args
     * @return
     */
    @Override
    public Object handle(RequestMeta requestMeta, Method method, Object[] args) {
        MetaData metaData = EsTool.getMetaData(requestMeta.getBaseEntity());
        RestClient client = restEsClient.getLowLevelClient();
        try {
            String dsl = paramHandler.handle(requestMeta.getRequestJson(), method, args);
            Request request = new Request("GET","/"+metaData.getIndexName()+"/_doc/_search");
            request.setJsonEntity(dsl);
            Response response = client.performRequest(request);
            return resultHandlerFactory.match(requestMeta.getType()).resolver(dsl,response.getEntity(),method);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * 匹配查询
     *
     * @param type
     * @return
     */
    @Override
    public boolean matching(EsSelectType type) {
        return Objects.equals(type,EsSelectType.DSL);
    }
}
