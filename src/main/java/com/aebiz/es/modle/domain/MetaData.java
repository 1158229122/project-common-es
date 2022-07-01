package com.aebiz.es.modle.domain;

/**
 * @author jim
 * @date 2022/6/8 14:35
 */
public class MetaData {
    private String indexName;
    private boolean createIndex;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public boolean isCreateIndex() {
        return createIndex;
    }

    public void setCreateIndex(boolean createIndex) {
        this.createIndex = createIndex;
    }
}
