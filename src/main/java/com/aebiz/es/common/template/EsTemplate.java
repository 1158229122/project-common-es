package com.aebiz.es.common.template;

import com.aebiz.es.modle.domain.Page;
import com.aebiz.es.modle.EsBaseEntity;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author jim
 * @date 2022/6/28 18:06
 */
public interface EsTemplate<T extends EsBaseEntity> {
    /**
     * 新增
     * @param t
     * @return
     */
    T create(T t);

    /**
     * 修改
     * @param t
     * @return
     */
    T update(T t);


    /**
     * 批量新增
     * @param list
     */
    List<T> batchCreate(List<T> list);

    /**
     * 批量修改
     * @param list
     */
    List<T> batchUpdate(List<T> list);

    /**
     * id查询
     * @param id
     * @param clazz
     * @return
     */
    T findById(Serializable id,Class<T> clazz);

    /**
     * 主键id删除
     * @param id
     * @param entityClazz
     * @return
     */
    boolean deleteById(Serializable id, Class<T> entityClazz);

    /**
     * 条件查询
     * @param searchSourceBuilder
     * @param clazz
     * @return
     */
    Page<T> queryPage(SearchSourceBuilder searchSourceBuilder, Class<T> clazz);

    /**
     * 条件删除
     * @param queryBuilder
     * @param clazz
     * @return
     */
    boolean deleteByCondition(QueryBuilder queryBuilder, Class<T> clazz);

    /**
     * 条件统计
     * @param queryBuilder
     * @param clazz
     * @return
     * @throws Exception
     */
    Long count(QueryBuilder queryBuilder, Class<T> clazz);

    /**
     * 刷新缓存
     */
    boolean refresh(Class<T> clazz);
}
