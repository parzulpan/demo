package cn.parzulpan.web;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @Author : parzulpan
 * @Time : 2020-12-13
 * @Desc : 使用 Filter 过滤器拦截/pages/manager/所有内容，实现权限检查
 */

@WebFilter(filterName = "ManagerFilter", urlPatterns = ("/pages/manager/*"))
public class ManagerFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
