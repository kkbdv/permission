package com.muke.common;

import com.muke.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class HttpInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI().toString();
        Map parameterMap = request.getParameterMap();
        log.info("request start url:{},params:{}",url, JsonMapper.obj2String(parameterMap));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception { //执行完Controller之后调用
        String url = request.getRequestURI().toString();
        Map parameterMap = request.getParameterMap();
        log.info("request finished url:{},params:{}",url, JsonMapper.obj2String(parameterMap));
        removeThreadLocalInfo();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String url = request.getRequestURI().toString();
        Map parameterMap = request.getParameterMap();
        log.info("request exception url:{},params:{}",url, JsonMapper.obj2String(parameterMap));

        removeThreadLocalInfo();
    }

    public void removeThreadLocalInfo(){ //清除元空间的static 变量，防止内存泄漏
        RequestHolder.remove();
    }
}
