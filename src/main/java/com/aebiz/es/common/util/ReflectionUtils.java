package com.aebiz.es.common.util;

import com.aebiz.es.modle.annotation.EsParam;
import org.apache.http.client.utils.DateUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author jim
 * @date 2022/6/30 13:44
 */
public class ReflectionUtils {

    public static EsParam findMethodParam(Method method,int index){
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (Annotation annotation : paramAnnotations[index]) {
            if (annotation instanceof EsParam) {
                return (EsParam) annotation;
            }
        }
        return null;
    }

    public static boolean isBaseTypeAndExtend(Class<?> type) {
        return isBaseType(type) || isExtendsType(type);
    }

    /**
     * judge the given type is Java-Base type or String, but not void.class
     *
     * @param type return
     */
    public static boolean isBaseType(Class<?> type) {
        return (type.isPrimitive() && !Objects.equals(type, void.class))
                || type.equals(String.class) || type.equals(Boolean.class)
                || type.equals(Integer.class) || type.equals(Long.class) || type.equals(Short.class)
                || type.equals(Float.class) || type.equals(Double.class)
                || type.equals(Byte.class) || type.equals(Character.class);
    }

    /**
     * 基本参数支持的扩展类型
     *
     * @param type
     * @return
     */
    public static boolean isExtendsType(Class<?> type) {
        return type.equals(LocalDateTime.class) || type.equals(LocalDate.class) || type.equals(BigDecimal.class);

    }

    /**
     * 获取对象的filed name 和 value,支持嵌套
     * file 的name 格式为 为 objectName.filedName的形式
     *
     * @param view
     * @param paramName
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, Object> getNestedFieldsMap(String paramName, Object view)  {
        Map<String, Object> map = new HashMap<>(26);
        try {
            getFields(view,map, paramName);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
    /**
     * 递归获取对象的 filed name 和 value
     * @param view
     * @param map
     * @param parentName
     * @throws IllegalAccessException
     */
    public static void getFields(Object view, Map<String, Object> map, String parentName) throws IllegalAccessException {
        Class<?> clazz = view.getClass();
        List<Field> fieldArr = new ArrayList();
        while (clazz != null){
            fieldArr.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        for (Field field : fieldArr) {
            field.setAccessible(true);
            String name = parentName + "." + field.getName();
            Object val = field.get(view);
            if(isBaseTypeAndExtend(field.getType())){
                Object parameterVal = val;
                map.put(name, parameterVal);
            }else{
                getFields(val,map,name);
            }
        }
    }
    /**
     * 参数解析
     * 如果参数是String，则添加单引号， 如果是日期，则转换为时间格式器并加单引号； 对参数是null和不是null的情况作了处理
     *
     * @param obj
     * @return
     */
    public static String getParameterValue(Object obj) {
        if (obj == null) {
            return "null";
        }
        String value;
        if (obj instanceof String) {
            value = "'" + obj + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(obj) + "'";
        } else {
            value = obj.toString();
        }
        return value;
    }

}
