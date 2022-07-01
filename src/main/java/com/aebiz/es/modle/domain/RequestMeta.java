package com.aebiz.es.modle.domain;

import com.aebiz.es.modle.EsBaseEntity;
import com.aebiz.es.modle.enums.EsSelectType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jim
 * @date 2022/6/30 18:25
 */
@Data
public class RequestMeta implements Serializable {
    private  Class<? extends EsBaseEntity> baseEntity;
    private Object proxy;
    private EsSelectType type;
    private String requestJson;
}
