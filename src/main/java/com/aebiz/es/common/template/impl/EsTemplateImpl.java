package com.aebiz.es.common.template.impl;

import com.aebiz.es.modle.domain.MetaData;
import com.aebiz.es.common.template.EsTemplate;
import com.aebiz.es.common.util.EsTool;
import com.aebiz.es.modle.domain.Page;
import com.aebiz.es.modle.EsBaseEntity;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author jim
 * @date 2022/6/28 18:07
 */
@Component
@AllArgsConstructor
public class EsTemplateImpl<T extends EsBaseEntity> implements EsTemplate<T> {
    private final RestHighLevelClient restEsClient;


    /**
     * 新增
     *
     * @param t
     * @return
     */
    @Override
    public T create(T t) {
        IndexRequest indexRequest = getIndexRequest(t);
        indexRequest.create(true);
        try {
            restEsClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return t;
    }

    /**
     * 修改
     *
     * @param t
     * @return
     */
    @Override
    public T update(T t) {
        IndexRequest indexRequest = getIndexRequest(t);
        indexRequest.create(false);
        try {
            restEsClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return t;
    }

    /**
     * 批量新增
     *
     * @param list
     */
    @Override
    public List<T> batchCreate(List<T> list) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (T t : list) {
                IndexRequest indexRequest = this.getIndexRequest(t);
                //设置create
                indexRequest.create(true);

                bulkRequest.add(indexRequest);
            }
            this.restEsClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            return list;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 批量修改
     *
     * @param list
     */
    @Override
    public List<T> batchUpdate(List<T> list) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (T t : list) {
                IndexRequest indexRequest = this.getIndexRequest(t);
                //设置create
                indexRequest.create(false);

                bulkRequest.add(indexRequest);
            }
            this.restEsClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            return list;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * id查询
     *
     * @param id
     * @param clazz
     * @return
     */
    @Override
    public T findById(Serializable id, Class clazz) {
        try {
            MetaData metaData = EsTool.getMetaData(clazz);
            GetRequest getRequest = new GetRequest(metaData.getIndexName());
            getRequest.id(Objects.toString(id));
            GetResponse getResponse = this.restEsClient.get(getRequest, RequestOptions.DEFAULT);
            T t = (T)JSON.parseObject(getResponse.getSourceAsString(), clazz);
            return t;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 主键id删除
     *
     * @param id
     * @param entityClazz
     * @return
     */
    @Override
    public boolean deleteById(Serializable id, Class<T> entityClazz) {
        try {
            MetaData metaData = EsTool.getMetaData(entityClazz);
            DeleteRequest deleteRequest = new DeleteRequest(metaData.getIndexName(), String.valueOf(id));
            DeleteResponse deleteResponse = this.restEsClient.delete(deleteRequest, RequestOptions.DEFAULT);
            return deleteResponse.getResult() == DocWriteResponse.Result.DELETED;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 条件查询
     *
     * @param searchSourceBuilder
     * @param clazz
     * @return
     */
    @Override
    public Page<T> queryPage(SearchSourceBuilder searchSourceBuilder, Class<T> clazz) {
        try{
            MetaData metaData = EsTool.getMetaData(clazz);
            SearchRequest searchRequest = new SearchRequest(metaData.getIndexName());
            List list = new ArrayList();
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = restEsClient.search(searchRequest, RequestOptions.DEFAULT);
            Page<T> page = new Page();
            int from = searchSourceBuilder.from();
            int size = searchSourceBuilder.size();
            page.setCurrent((from/size)+1);
            page.setTotal(searchResponse.getHits().getTotalHits().value);
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                T t = JSON.parseObject(hit.getSourceAsString(), clazz);
                list.add(t);
            }
            page.setRecords(list);
            return page;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }



    /**
     * 条件删除
     *
     * @param queryBuilder
     * @param clazz
     * @return
     */
    @Override
    public boolean deleteByCondition(QueryBuilder queryBuilder, Class<T> clazz) {
        try {
            MetaData metaData = EsTool.getMetaData(clazz);
            DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(metaData.getIndexName());
            deleteByQueryRequest.setQuery(queryBuilder);
            restEsClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
            return true;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 条件统计
     *
     * @param queryBuilder
     * @param clazz
     * @return
     * @throws Exception
     */
    @Override
    public Long count(QueryBuilder queryBuilder, Class<T> clazz) {
        try {
            MetaData metaData = EsTool.getMetaData(clazz);
            CountRequest countRequest = new CountRequest(metaData.getIndexName());
            countRequest.query(queryBuilder);
            CountResponse response = restEsClient.count(countRequest, RequestOptions.DEFAULT);
            return response.getCount();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 刷新缓存
     */
    @Override
    public boolean refresh(Class<T> clazz) {
        MetaData metaData = EsTool.getMetaData(clazz);
        try {
            RefreshRequest refreshRequest = new RefreshRequest(metaData.getIndexName());
            RefreshResponse refresh = this.restEsClient.indices().refresh(refreshRequest, RequestOptions.DEFAULT);
            return true;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }


    private IndexRequest getIndexRequest(T entity) {

        MetaData metaData = EsTool.getMetaData(entity.getClass());
        IndexRequest indexRequest = new IndexRequest(metaData.getIndexName());
        String id = entity.getUuid();
        if (id == null) {
            id = this.genUuid();
            entity.setUuid(id);
        }
        String source = JSON.toJSONString(entity);
        indexRequest.id(id).source(source, XContentType.JSON);
        return indexRequest;
    }

    public static String genUuid() {
        String str = UUID.randomUUID().toString();
        return str.replaceAll("-", "");
    }


}
