package com.muke.filter;

import com.muke.common.RequestHolder;
import com.muke.model.SysUser;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截用户登陆请求
 */
@Slf4j
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String servletPath = req.getServletPath();
        SysUser sysUser = (SysUser) req.getSession().getAttribute("user");
        if(sysUser==null){//判断请求是否有用户信息
            String path ="/signin.jsp"; // ‘/’指定前缀告诉系统从顶层开始访问，否则在当前路径下访问
            resp.sendRedirect(path);//客户端强制跳转,走登陆流程
            return;
        }else {
            RequestHolder.add(sysUser); //放到threadLocal
            RequestHolder.add(req);
            filterChain.doFilter(servletRequest,servletResponse); //添加成功后调用过滤链
            return;
        }

    }

    @Override
    public void destroy() {

    }
}
