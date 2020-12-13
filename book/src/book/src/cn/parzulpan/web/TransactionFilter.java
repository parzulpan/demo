package cn.parzulpan.web;

import cn.parzulpan.utils.JDBCUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * @Author : parzulpan
 * @Time : 2020-12-13
 * @Desc : 事务过滤器
 */

public class TransactionFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        try {
            chain.doFilter(req, resp);
            JDBCUtils.commitAndClose();
        } catch (IOException e) {
            JDBCUtils.rollbackAndClose();
            e.printStackTrace();
            throw new RuntimeException(e);  // 让 Tomcat 展示友好的错误信息页面
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
