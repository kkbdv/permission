package com.muke.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 自定义工具类，只适合本程序用
 */
public class LevelUtil {

    //分隔符
    public final static  String SEPARATOR = ".";

    public final static String ROOT = "0";

    //0
    //0.1
    //0.1.2
    //0.2
    public static String calculateLevel(String parentLevel,int parentId){
        if(StringUtils.isBlank(parentLevel)){ //true 为首层
            return ROOT;
        }else {
            return StringUtils.join(parentLevel,SEPARATOR,parentId);
        }
    }
}
