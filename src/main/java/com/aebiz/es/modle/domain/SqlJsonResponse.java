package com.aebiz.es.modle.domain;

import lombok.Data;

import java.util.List;

/**
 * @author jim
 * @date 2022/6/30 15:12
 */
@Data
public class SqlJsonResponse extends SqlErrorResponse {
    private List<ColumnsDTO> columns;
    private List<List<String>> rows;


    @Data
    public static class ColumnsDTO {
        private String name;
        private String type;
    }
}
