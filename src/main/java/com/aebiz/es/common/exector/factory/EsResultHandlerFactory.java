package com.aebiz.es.common.exector.factory;

import com.aebiz.es.common.result.EsResultHandler;
import com.aebiz.es.modle.enums.EsSelectType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jim
 * @date 2022/6/29 18:34
 */
@Component
@AllArgsConstructor
public class EsResultHandlerFactory {
    private final List<EsResultHandler> list;

    public EsResultHandler match(EsSelectType type){
        for (EsResultHandler handler : list) {
            if (handler.matching( type)){
                return handler;
            }
        }
        throw new RuntimeException("未找到对应的es结果处理器");
    }
}
