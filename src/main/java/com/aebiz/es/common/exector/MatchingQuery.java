package com.aebiz.es.common.exector;

import com.aebiz.es.modle.enums.EsSelectType;

/**
 * @author jim
 * @date 2022/6/29 17:33
 */
public interface MatchingQuery {
    /**
     * 匹配查询
     * @param type
     * @return
     */
    boolean matching(EsSelectType type);
}
