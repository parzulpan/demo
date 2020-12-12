package cn.parzulpan.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Author : parzulpan
 * @Time : 2020-12-12
 * @Desc :
 */

@WebServlet(name = "SessionServlet", urlPatterns = ("/sessionServlet"))
public class SessionServlet extends BaseServlet {
    // 创建和获取 Session
    protected void createOrGetSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        boolean isNew = session.isNew();
        String id = session.getId();

        response.getWriter().write("得到的 Session 的 Id 是：" + id + "<br>");
        response.getWriter().write("这个 Session 是否是新创建的：" + isNew + "<br>");
    }

    // 向 Session 中保存数据
    protected void setAttribute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().setAttribute("key1", "value1");
        response.getWriter().write("已经往 Session 中保存了数据");
    }

    // 获取 Session 域中的数据
    protected void getAttribute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object key1 = request.getSession().getAttribute("key1");
        response.getWriter().write("从 Session 中获取出 key1 的数据是：" + key1);
    }

    // Session 默认超时，默认超时时长为 30 分钟，可以通过 Tomcat 的 web.xml 配置更改
    protected void defaultSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int maxInactiveInterval = request.getSession().getMaxInactiveInterval();
        response.getWriter().write("Session 默认超时时长：" + maxInactiveInterval);
    }

    // Session 10s 后超时
    protected void life10Session(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(10);
        response.getWriter().write("当前 Session 已经设置为 10 秒后超时");
    }

    // Session 马上超时
    protected void deleteSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 先获取 Session 对象
        HttpSession session = request.getSession();
        // 让 Session 会话马上超时
        session.invalidate();
        response.getWriter().write("Session 已经设置为超时（无效）");
    }
}
