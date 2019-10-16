package com.muke.util;


import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

/**
 * 把一个类转化成json对象，或把json对象转化成一个类对象
 */
@Slf4j
public class JsonMapper {
    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        //config
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES);//不认识的字段如何处理
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);//排除为空的字段
    }

    public static <T> String obj2String(T src){
        if(src==null){
            return null;
        }
        try{
            return src instanceof String ? (String)src : objectMapper.writeValueAsString(src);
        }catch (Exception e){
            log.warn("parse object to String exception,error:{}",e);
            return null;//暂时抛出为空
        }
    }

    public static <T> T string2Obj(String src, TypeReference<T> typeReference){
        if(src == null || typeReference == null){
            return null;
        }
        try {
            return (T)  (typeReference.getType().equals(String.class)? src:objectMapper.readValue(src,typeReference)); //注意这里的表达式
        }catch (Exception e){
            log.warn("parse String to Object exception ,String:{},TypeReference<T>:{},error:{}",src,typeReference,e);
            return null;
        }

    }


}
