package com.aebiz.es.modle.support;


import com.aebiz.es.modle.EsBaseEntity;
import com.aebiz.es.modle.domain.Page;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * @author jim
 * @date 2022/6/28 14:41
 */
public interface BaseEsRepository<T extends EsBaseEntity> extends BaseCurdRepository<T> {

    /**
     * 强制刷新缓存，用于es新增有立马修改
     */
    void refresh();

    /**
     * 分页查询
     * @param searchSourceBuilder
     * @return
     */
    Page<T> queryPage(SearchSourceBuilder searchSourceBuilder);

    /**
     * 统计
     * @param queryBuilder
     * @return
     */
    long count(QueryBuilder queryBuilder);
}
