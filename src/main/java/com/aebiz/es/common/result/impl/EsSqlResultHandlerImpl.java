package com.aebiz.es.common.result.impl;

/**
 * @author jim
 * @date 2022/6/29 18:31
 */

import com.aebiz.es.common.result.EsResultHandler;
import com.aebiz.es.common.util.ReflectionUtils;
import com.aebiz.es.modle.domain.Page;
import com.aebiz.es.modle.domain.SqlJsonResponse;
import com.aebiz.es.modle.enums.EsSelectType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Component
@AllArgsConstructor
public class EsSqlResultHandlerImpl implements EsResultHandler {
    @Override
    public Object resolver(String originRequest,final HttpEntity entity, Method method) {
        if (entity == null) {
            throw new IllegalStateException("Response body expected but not returned");
        }
        if (entity.getContentType() == null) {
            throw new IllegalStateException("Elasticsearch didn't return the [Content-Type] header, unable to parse response body");
        }

        List<JSONObject> list = this.convertEsResponse(entity);
        Class<?> returnType = method.getReturnType();
        if (Page.class.isAssignableFrom(returnType)){
            //分页对象
            Page page = new Page();
            List returnList = new ArrayList();
            this.convertCollection(method, list, returnList);
            page.setRecords(list);
            return page;
        }else if (List.class.isAssignableFrom(returnType)){
            List returnList = new ArrayList();
            this.convertCollection(method, list, returnList);
            return list;
        }else if (Set.class.isAssignableFrom(returnType)){
            Set set = new HashSet();
            this.convertCollection(method, list, set);
            return set;
        }else if (returnType.isArray()){

            return this.convertArray(method, list);
        }else {
            return this.convertObj(method,list);
        }
    }

    private Object convertObj(Method method, List<JSONObject> list) {
        Class<?> returnType = method.getReturnType();

        if (list.size()==0){
            return null;
        }else if (list.size()==1){
            if (Map.class.isAssignableFrom(returnType)){
                return list.get(0);
            }else if (ReflectionUtils.isBaseTypeAndExtend(returnType)){
                //基础类型转
                JSONObject jsonObject = list.get(0);
                Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
                Object value = null;
                for (Map.Entry<String, Object> entry : entries) {
                    value = entry.getValue();
                }
                if (value==null){
                    return null;
                }

                return JSON.parseObject(value.toString(),method.getGenericReturnType());
            }else {
                return JSON.parseObject(list.get(0).toJSONString(),method.getGenericReturnType());
            }
        }else {
            throw new RuntimeException("期望找到1个，但是找到了"+list.size()+"条记录");
        }

    }

    private Object convertArray(Method method, List<JSONObject> list) {
        Class<?> returnType = method.getReturnType();
        Class<?> componentType = returnType.getComponentType();

        Object arr = Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            if (Map.class.isAssignableFrom(componentType)){
                Array.set(arr,i,list.get(i));
            }else {
                Array.set(arr,i,JSON.parseObject(list.get(i).toJSONString()));
            }
        }

        return arr;
    }

    private Collection convertCollection(Method method, List<JSONObject> list, Collection returnList) {
        Type actualType = this.getMethodActualType(method);
        for (JSONObject jsonObject : list) {
            if (Objects.nonNull(actualType)){
                //根据返回类型设置值
                returnList.add(JSON.parseObject(jsonObject.toJSONString(), actualType));
            }else {
                //未指定按Map来转
                returnList.add(jsonObject.toJSONString());
            }
        }
        return returnList;
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


    private List<JSONObject> convertEsResponse(HttpEntity entity){
        try {
            SqlJsonResponse sqlJsonResponse = JSON.parseObject(EntityUtils.toString(entity), SqlJsonResponse.class);
            if (Objects.nonNull(sqlJsonResponse.getStatus())) {
                throw new IllegalStateException("SQL查询异常:  " +JSON.toJSONString(sqlJsonResponse));
            }
            List<JSONObject> result = new ArrayList<>();
            List<SqlJsonResponse.ColumnsDTO> columns = sqlJsonResponse.getColumns();
            if (!CollectionUtils.isEmpty(sqlJsonResponse.getRows())) {
                for (List<String> rows: sqlJsonResponse.getRows()) {
                    JSONObject jsonObject = new JSONObject();
                    for (int i =    0; i < rows.size(); i++) {
                        String row = rows.get(i);
                        jsonObject.put(columns.get(i).getName(),row);
                    }
                    result.add(jsonObject);
                }
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
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
        return Objects.equals(type,EsSelectType.SQL);
    }
}
