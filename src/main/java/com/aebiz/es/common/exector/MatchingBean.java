package com.aebiz.es.common.exector;

/**
 * @author jim
 * @date 2022/6/28 17:43
 */
public interface MatchingBean {
    /**
     * 匹配查找对应的es执行器
     * @param clazz
     * @return
     */
    boolean matching(Class clazz);
}
