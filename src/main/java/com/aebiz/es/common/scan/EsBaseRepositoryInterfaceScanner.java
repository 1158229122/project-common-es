package com.aebiz.es.common.scan;

import com.aebiz.es.common.metadata.EsMetadata;
import com.aebiz.es.common.proxy.EsBeanProxyFactory;
import com.aebiz.es.common.util.FindClassUtil;
import com.aebiz.es.modle.support.BaseRepository;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * BaseRepository 扫描接口
 * @author jim
 * @date 2022/6/28 14:43
 */
@Configuration
public class EsBaseRepositoryInterfaceScanner implements ApplicationContextAware, ResourceLoaderAware, BeanDefinitionRegistryPostProcessor {

    private ApplicationContext applicationContext;
    private MetadataReaderFactory metadataReaderFactory;
    private ResourcePatternResolver resourcePatternResolver;
    private final String RESOURCE_PATTERN = "/**/*.class";


    @SneakyThrows
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<Class<?>> interfaceSet = FindClassUtil.findInterface(applicationContext, BaseRepository.class);
        for (Class<?> beanClazz : interfaceSet) {
            Type[] genericInterfaces = beanClazz.getGenericInterfaces();
            for (Type type : genericInterfaces) {
                if ( type instanceof ParameterizedType){
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Class rawType = (Class) parameterizedType.getRawType();
                    if (BaseRepository.class.isAssignableFrom(rawType)){
                        Class actualTypeArgument = (Class) parameterizedType.getActualTypeArguments()[0];
                        EsMetadata.putBaseEntity(beanClazz,actualTypeArgument);
                    }
                }
            }
            //bean register
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClazz);
            definition.setBeanClass(beanClazz);
            definition.setLazyInit(true);
            definition.setInstanceSupplier(() ->
                    new EsBeanProxyFactory(beanClazz)
            );
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            String simpleName = beanClazz.getSimpleName();
            simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
            registry.registerBeanDefinition(simpleName, definition);

        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }



}
