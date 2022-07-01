package com.aebiz.es.common.util;

import com.aebiz.es.modle.domain.MetaData;
import com.aebiz.es.modle.annotation.EsMetaData;
import java.util.Objects;

/**
 * TODO 注解映射关系待书写
 * @author jim
 * @date 2022/6/8 14:35
 */
public class EsTool {



    public static MetaData getMetaData(Class<?> clazz){
        if (Objects.isNull(clazz)) throw new RuntimeException("参数有误");
        MetaData metaData = new MetaData();
        EsMetaData esMetaData = (EsMetaData)clazz.getAnnotation(EsMetaData.class);
        if (Objects.isNull(esMetaData)) throw new RuntimeException("Es映射实体对象注解有误");
        metaData.setCreateIndex(esMetaData.createIndex());
        metaData.setIndexName(esMetaData.indexName());
        return metaData;
    }


}
