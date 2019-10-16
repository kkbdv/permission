package com.muke.common;

import com.muke.exception.ParamException;
import com.muke.exception.PermissionException;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {

    /**
     * 捕获springmvc全局异常
     */

    @Override
    public ModelAndView resolveException(HttpServletRequest request,  HttpServletResponse response, Object o, Exception e) {
        String url = request.getRequestURI().toString();
        ModelAndView mv;
        String defaultMsg = "System error";

        //这一步要通过 判断request 是数据请求 还是页面请求
        //方法一: 通过header 判断是文本请求还是页面请求  复杂
        //方法二: 通过 约定接口使用结尾为 .json , .page
        if(url.endsWith(".json")){ //要求所有请求数据的用.json结尾
            if(e instanceof PermissionException || e instanceof ParamException){//是自定义异常
                JsonData result = JsonData.fail(e.getMessage());
                mv = new ModelAndView("jsonView",result.toMap());
            }else{//默认异常
                log.error("unknow json exception,Url:"+url,e); //在日志中记录异常
                JsonData result = JsonData.fail(defaultMsg);
                mv = new ModelAndView("jsonView",result.toMap());
            }

        }else if(url.endsWith(".page")){ //要求所有请求页面的用.page结尾
            log.error("unknow page exception,Url:"+url,e);
            JsonData result = JsonData.fail();
            mv = new ModelAndView("exception",result.toMap()); //补充一个exception.jsp页面
        }else{ //不知道是什么请求的时候，返回json格式的异常
            log.error("unknow exception,Url:"+url,e);
            JsonData result = JsonData.fail(defaultMsg);
            mv = new ModelAndView("jsonView",result.toMap());
        }

        return mv;
    }
}
