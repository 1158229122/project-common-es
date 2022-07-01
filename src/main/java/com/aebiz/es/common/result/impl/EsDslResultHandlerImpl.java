package com.aebiz.es.common.result.impl;

import com.aebiz.es.common.result.EsResultHandler;
import com.aebiz.es.modle.domain.Page;
import com.aebiz.es.modle.enums.EsSelectType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import org.apache.http.HttpEntity;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.CheckedFunction;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author jim
 * @date 2022/6/29 18:30
 */
@Component
@AllArgsConstructor
public class EsDslResultHandlerImpl<R> implements EsResultHandler {

    private final NamedXContentRegistry registry =  new NamedXContentRegistry(Collections.emptyList());

    private static final DeprecationHandler DEPRECATION_HANDLER = new DeprecationHandler() {
        @Override
        public void usedDeprecatedName(String usedName, String modernName) {}
        @Override
        public void usedDeprecatedField(String usedName, String replacedWith) {}
    };

    @Override
    public Object resolver(String originRequest,final HttpEntity entity, Method method) {
        try {
            //转成 searchResponse
            SearchResponse searchResponse = this.parseEntity(entity, SearchResponse::fromXContent);
            //方法返回值
            Class<?> returnType = method.getReturnType();
            if (Page.class.isAssignableFrom(returnType)){
                //分页对象
                SearchHits hits = searchResponse.getHits();
                Page<R> page = new Page();
                page.setTotal(hits.getTotalHits().value);
                List<R> list = new ArrayList();
                this.convertCollection(method, searchResponse, list);
                page.setRecords(list);
                JSONObject jsonObject = JSONObject.parseObject(originRequest);
                Integer from = jsonObject.getInteger("from");
                Integer size = jsonObject.getInteger("size");
                if (from != null&&size!=null){
                    page.setCurrent((from/size)+1);
                    page.setSize(size);
                }
                return page;
            }else if (List.class.isAssignableFrom(returnType)){
                List<R> list = new ArrayList();
                this.convertCollection(method, searchResponse, list);
                return list;
            }else if (Set.class.isAssignableFrom(returnType)){
                Set<R> set = new HashSet();
                this.convertCollection(method, searchResponse, set);
                return set;
            }else if (returnType.isArray()){
                return this.convertArray(method, searchResponse);
            }else {
                return this.convertObj(method,searchResponse);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Object convertArray(Method method, SearchResponse searchResponse) {
        Class<?> returnType = method.getReturnType();
        Class<?> componentType = returnType.getComponentType();
        Object arr = Array.newInstance(componentType, searchResponse.getHits().getHits().length);
        for (int i = 0; i < searchResponse.getHits().getHits().length; i++) {
            SearchHit hit = searchResponse.getHits().getHits()[i];
            if (Map.class.isAssignableFrom(componentType)){
                Array.set(arr,i,JSON.parseObject(hit.getSourceAsString()));
            }else {
                Array.set(arr,i,JSON.parseObject(hit.getSourceAsString(),componentType));
            }
        }
        return arr;
    }

    private Collection convertCollection(Method method, SearchResponse searchResponse,Collection collection) {
        Type actualType = this.getMethodActualType(method);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            if (Objects.nonNull(actualType)){
                //根据返回类型设置值
                collection.add(JSON.parseObject(hit.getSourceAsString(), actualType));
            }else {
                //未指定按Map来转
                collection.add(JSON.parseObject(hit.getSourceAsString()));
            }
        }
        return collection;
    }

    private Object convertObj(Method method,SearchResponse searchResponse) {
        Class<?> returnType = method.getReturnType();
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length==0){
            return null;
        }else if (hits.length==1){
            if (Map.class.isAssignableFrom(returnType)){
                return JSON.parseObject(hits[0].getSourceAsString());
            }else {
                return JSON.parseObject(hits[0].getSourceAsString(),method.getGenericReturnType());
            }
        }else {
            throw new RuntimeException("期望找到1个，但是找到了"+hits.length+"条记录");
        }

    }


    private final <Resp> Resp parseEntity(final HttpEntity entity,
                                  final CheckedFunction<XContentParser, Resp, IOException> entityParser) throws IOException {
        if (entity == null) {
            throw new IllegalStateException("Response body expected but not returned");
        }
        if (entity.getContentType() == null) {
            throw new IllegalStateException("Elasticsearch didn't return the [Content-Type] header, unable to parse response body");
        }
        XContentType xContentType = XContentType.fromMediaTypeOrFormat(entity.getContentType().getValue());
        if (xContentType == null) {
            throw new IllegalStateException("Unsupported Content-Type: " + entity.getContentType().getValue());
        }
        try (XContentParser parser = xContentType.xContent().createParser(registry, DEPRECATION_HANDLER, entity.getContent())) {
            return entityParser.apply(parser);
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

    /**
     * 查找方法返回值泛型类型
     * @param method
     * @return
     */
    private final Type getMethodActualType(Method method){
        Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType instanceof ParameterizedType){
            Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
            if (Objects.nonNull(actualTypeArguments)&&actualTypeArguments.length>0){
                return ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
            }
        }
        return null;
    }
}
