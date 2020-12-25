package cn.parzulpan.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户登录拦截器
 */

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("called LoginInterceptor preHandle...");

        //如果是登录页面则放行
        if (request.getRequestURI().contains("login")) {
            System.out.println("登录页面");
            return true;
        }

        // 如果用户已登录则放行
        HttpSession session = request.getSession();
        if (session.getAttribute("activeUser") != null) {
            System.out.println("用户已登录");
            return true;
        }

        // 用户没有登录跳转到登录页面
        System.out.println("用户没有登录");
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);

        return false;
    }
}
