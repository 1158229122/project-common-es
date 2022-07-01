package com.aebiz.es.common.util;

import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 查询项目的工具类
 * @author jim
 * @date 2022/6/28 15:05
 */
public class FindClassUtil {
    private static final String RESOURCE_PATTERN = "/**/*.class";

    public static Set<Class<?>> findAllClass(ApplicationContext applicationContext){
        //拿到的package 是根路径 com.elasticsearch.engine.elasticsearchengine
        List<String> packages = AutoConfigurationPackages.get(applicationContext);
        Set<Class<?>> set = new HashSet<>();
        for (String basePackage : packages) {
            //spring工具类，可以获取指定路径下的全部类
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            try {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        ClassUtils.convertClassNameToResourcePath(basePackage) + RESOURCE_PATTERN;
                Resource[] resources = resourcePatternResolver.getResources(pattern);
                //MetadataReader 的工厂类
                MetadataReaderFactory readerfactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                for (Resource resource : resources) {
                    if (resource.isReadable()){
                        //用于读取类信息
                        MetadataReader reader = readerfactory.getMetadataReader(resource);
                        //扫描到的class
                        String classname = reader.getClassMetadata().getClassName();
                        Class<?> clazz = Class.forName(classname);
                        set.add(clazz);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return set;
    }

    public static Set<Class<?>> findAllInterface(ApplicationContext applicationContext){
        Set<Class<?>> allClass = findAllClass(applicationContext);
        allClass.removeIf(aClass -> !aClass.isInterface());
        return allClass;
    }

    public static Set<Class<?>> findInterface(ApplicationContext applicationContext,Class clazz){
        Set<Class<?>> allClass = findAllClass(applicationContext);
        allClass.removeIf(aClass -> !(aClass.isInterface() && clazz.isAssignableFrom(aClass)));
        return allClass;
    }


}
