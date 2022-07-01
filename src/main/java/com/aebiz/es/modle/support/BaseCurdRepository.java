package com.aebiz.es.modle.support;


import com.aebiz.es.modle.EsBaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * @author jim
 * @date 2022/6/28 16:26
 */
public interface BaseCurdRepository<T extends EsBaseEntity> extends BaseRepository {
    /**
     * 新增或修改
     * @param t
     */
    T create(T t);

    /**
     * 批量新增
     * @return
     */
    List<T> batchCreate(List<T> list);

    /**
     * 新增或修改
     * @param t
     */
    T update(T t);

    /**
     * 批量修改
     * @return
     */
    List<T> batchUpdate(List<T> list);

    /**
     * 主键id查询
     * @param id
     * @return
     */
    T findById(Serializable id);

    /**
     * 主键id查询
     * @param id
     * @return
     */
    boolean deleteById(Serializable id);
}
