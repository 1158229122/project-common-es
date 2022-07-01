package com.aebiz.es.common.metadata;

import com.aebiz.es.modle.EsBaseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * es 源数据
 * @author jim
 * @date 2022/6/29 15:17
 */
public class EsMetadata {
    private static final Map<Class,Class<? extends EsBaseEntity>> CACHE_ENTITY_CLASS = new HashMap<>();
    private static final Map<Class,String> CACHE_INDEX_NAME = new HashMap<>();

    public static void putBaseEntity(Class interfaceClass,Class<? extends EsBaseEntity> entity){
        CACHE_ENTITY_CLASS.put(interfaceClass,entity);
    }

    public static Class<? extends EsBaseEntity> getBaseEntity(Class interfaceClass){
        return CACHE_ENTITY_CLASS.get(interfaceClass);
    }


}
