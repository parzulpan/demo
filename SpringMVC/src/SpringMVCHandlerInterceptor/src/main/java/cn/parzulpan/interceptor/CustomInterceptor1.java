package cn.parzulpan.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义拦截器
 */

public class CustomInterceptor1 implements HandlerInterceptor {

    /**
     * 预处理，Controller 方法执行前
     * 可以使用转发或者重定向直接跳转到指定的页面
     * @param request
     * @param response
     * @param handler
     * @return true 代表放行，执行下一个拦截器，如果没有则执行 Controller 中的方法；false 代表不放行
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("called CustomInterceptor1 preHandle...");

//        request.getRequestDispatcher("/WEB_INF/views/error.jsp").forward(request, response);

        return true;
    }

    /**
     * 后处理，Controller 方法执行后，success.jsp 执行前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("called CustomInterceptor1 postHandle...");

//        request.getRequestDispatcher("/WEB_INF/views/error.jsp").forward(request, response);
    }

    /**
     * success.jsp 执行后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("called CustomInterceptor1 afterCompletion...");
    }
}
