package com.aebiz.es.modle.domain;

import lombok.Data;

/**
 * @author jim
 * @date 2022/6/30 15:12
 */
@Data
public class SqlErrorResponse {

    /**
     * 错误信息
     */
    private ErrorDTO error;

    /**
     * 响应状态
     */
    private Integer status;


    @Data
    public static class ErrorDTO{
        String type;
        String reason;
    }
}
