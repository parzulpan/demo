package cn.parzulpan.web;

import cn.parzulpan.utils.CookieUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author : parzulpan
 * @Time : 2020-12-12
 * @Desc :
 */

@WebServlet(name = "CookieServlet", urlPatterns = ("/cookieServlet"))
public class CookieServlet extends BaseServlet {

    // 创建 Cookie
    protected void createCookie(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 创建 Cookie 对象
        Cookie cookie = new Cookie("key1", "value1");
        // 2. 通知客户端保存 Cookie
        response.addCookie(cookie);
        // 1. 创建 Cookie 对象
        Cookie cookie2 = new Cookie("key2", "value2");
        // 2. 通知客户端保存 Cookie
        response.addCookie(cookie2);
        response.getWriter().write("Cookie 创建成功");
    }

    // 获取 Cookie
    protected void getCookie(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取客户端发送过来的 Cookie
        Cookie[] cookies = request.getCookies();
        response.getWriter().write("Cookie 获取成功");

        for (Cookie cookie : cookies) {
            response.getWriter().write("Cookie[" + cookie.getName() + "=" + cookie.getValue() + "] <br/>");
        }

        Cookie iWantCookie = CookieUtils.findCookie("key1", cookies);
        if (iWantCookie != null) {
            response.getWriter().write("找到了需要的 key1 Cookie: " + iWantCookie.getValue());
        }
    }

    // 修改 Cookie
    protected void updateCookie(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 方案一：
        //1、先创建一个要修改的同名（指的就是 key）的 Cookie 对象
        //2、在构造器，同时赋于新的 Cookie 值
        Cookie newCookie = new Cookie("key1", "new-value1");
        //3、调用 response.addCookie( Cookie ) 通知客户端保存修改
        response.addCookie(newCookie);

        // 方案二：
        //1、先查找到需要修改的 Cookie 对象
        Cookie newCookie2 = CookieUtils.findCookie("key2", request.getCookies());
        //2、调用 setValue()方法赋于新的 Cookie 值
        if (newCookie2 != null) {
            newCookie2.setValue("new-value2");
        }
        //3、调用 response.addCookie( Cookie ) 通知客户端保存修改
        response.addCookie(newCookie2);

        response.getWriter().write("Cookie 修改成功");
    }


    // Cookie 存活 120s。
    protected void life120Cookie(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie cookie = new Cookie("life120Cookie", "life120Cookie");
        cookie.setMaxAge(60 * 2);
        response.addCookie(cookie);
        response.getWriter().write("已经创建了一个存活二分钟的 life120Cookie");
    }

    // Cookie 存活 0s，即删除。
    protected void deleteCookie(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie cookie = CookieUtils.findCookie("defaultCookie", request.getCookies()) ;
        if (cookie != null) {
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            response.getWriter().write("defaultCookie 已经被删除了");
        }

    }

    // Cookie 默认的会话级别 session。
    protected void defaultCookie(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie cookie = new Cookie("defaultCookie", "defaultCookie");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
        response.getWriter().write("已经创建了一个默认的 defaultCookie");
    }

    // 路径设置
    protected void setPath(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie cookie = new Cookie("path1", "path1");
        cookie.setPath(request.getContextPath() + "AA");    // 得到工程路径
        response.addCookie(cookie);
        response.getWriter().write("创建了一个带有 Path 路径的 Cookie");
    }

    // 免输入用户名登录
    protected void loginWithCookie(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ("parzulpan".equals(username) && "123456".equals(password)) {
            // 登录成功
            Cookie cookie = new Cookie("username", username);
            cookie.setMaxAge(60 * 60 * 24 * 7); // 当前 Cookie 一周内有效
            response.addCookie(cookie);
            System.out.println("登录成功");
        } else {
            System.out.println("登录失败");
        }
    }
}