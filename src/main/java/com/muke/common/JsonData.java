package com.muke.common;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 *  定义一个json返回类，封装实体和一些描述信息
 */
@Getter
@Setter
public class JsonData {

    private boolean ret;
    private String msg;
    private Object data;

    public JsonData(boolean ret){//只传入结果
        this.ret = ret;
    }

    //定义两个常用的成功状态
    public static JsonData success(Object object,String msg){
        JsonData jsonData = new JsonData(true);
        jsonData.data = object;
        jsonData.msg = msg;
        return jsonData;
    }

    public static JsonData success(Object objet){
        JsonData jsonData = new JsonData(true);
        jsonData.data = objet;
        return jsonData;
    }

    public static JsonData success(){
        return new JsonData(true);
    }

    //定义失败方法
    public  static JsonData fail(String msg){
        JsonData jsonData = new JsonData(false);
        jsonData.msg = msg;
        return jsonData;
    }

    public  static  JsonData fail(){
        return new JsonData(false);
    }

    //  转成Map的格式，让ModelAndView 返回使用
    public Map<String,Object> toMap(){
        HashMap<String ,Object> result = new HashMap<>();
        result.put("ret",this.ret);
        result.put("data",this.data);
        result.put("msg",this.msg);
        return  result;
    }

}
