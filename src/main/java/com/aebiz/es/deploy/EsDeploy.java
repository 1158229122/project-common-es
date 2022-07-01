//package com.aebiz.es.deploy;
//
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.core.io.support.ResourcePatternResolver;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
///**
// * es部署
// * @author jim
// * @date 2022/5/6 15:12
// */
//@Component
//@AllArgsConstructor
//@Slf4j
//public class EsDeploy {
//
//    private final EsDAO esDAO;
//
//    private final String esDeploy = "esdeploy/**/*.json";
//
//    @PostConstruct
//    public void createEsIndex() {
//
//        Resource[] resources = this.getResources(esDeploy);
//        for (Resource resource : resources) {
//            InputStream inputStream = null;
//            try {
//                inputStream = resource.getInputStream();
//                String json = this.readStreamToString(inputStream);
//                JSONObject jsonObject = JSON.parseObject(json);
//                String indexName = jsonObject.getString("indexName");
//                boolean existIndex = esDAO.exists(indexName);
//                if (!existIndex){
//                    String settings = jsonObject.getString("settings");
//                    String mappings = jsonObject.getString("mappings");
//                    log.info("create es index -> {}",indexName);
//                    esDAO.createIndex(indexName,settings,mappings);
//                    log.info("es index  create success -> {}",indexName);
//                }else {
//                    log.info("es index -> {}  exist",indexName);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }finally {
//                IoUtil.close(inputStream);
//            }
//
//        }
//    }
//
//    private Resource[] getResources(String location) {
//        try {
//            ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
//            return resourceResolver.getResources(location);
//        } catch (IOException var3) {
//            return new Resource[0];
//        }
//    }
//
//    /**
//     * 将流转换成一个字符串
//     *
//     * @param stream 流
//     * @return
//     */
//    private String readStreamToString(InputStream stream) {
//        StringBuffer fileContentsb = new StringBuffer();
//        String fileContent = "";
//
//        try {
//            InputStreamReader read = new InputStreamReader(stream, "utf-8");
//            BufferedReader reader = new BufferedReader(read);
//            String line;
//            while ((line = reader.readLine()) != null) {
//                fileContentsb.append(line + "\n");
//            }
//            read.close();
//            read = null;
//            reader.close();
//            read = null;
//            fileContent = fileContentsb.toString();
//        } catch (Exception ex) {
//            fileContent = "";
//        }
//        return fileContent;
//    }
//}
