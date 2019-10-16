package com.muke.util;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;

public class StringUtil {//把字符传转成列表

    //1,2,3,4,, -->[1,2,3,4]
    public static List<Integer> splitToListInt(String str){
        List<String> strList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(str);
       return strList.stream().map(strItem ->Integer.parseInt(strItem)).collect(Collectors.toList()); //不考虑出现字母的情况
    }
}
