package cn.parzulpan.component;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 登录拦截器
 */

public class LoginHandleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("loginUser");
        if (user == null) {
            // 未登录，提示信息，并返回登录页面
            request.setAttribute("msg", "没有权限！请先登录！");
            request.getRequestDispatcher("/index").forward(request, response);
            return false;
        } else {
            // 已登录，放行请求
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
