package cn.parzulpan.web;

import cn.parzulpan.bean.User;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author : parzulpan
 * @Time : 2020-12-14
 * @Desc :
 */

@WebServlet(name = "AjaxServlet", urlPatterns = ("/ajaxServlet"))
public class AjaxServlet extends BaseServlet {
    protected void javaScriptAjax(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Ajax 请求发过来了");

        // 将数据返回给客户端
        User user = new User(1, "潘K", "123456", "parzulpan@321.com");
        Gson gson = new Gson();
        String userJsonString = gson.toJson(user);
        response.getWriter().write(userJsonString);
    }

    protected void jQueryAjax(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("jQueryAjax 请求发过来了");

        // 将数据返回给客户端
        User user = new User(1, "潘K", "123456", "parzulpan@321.com");
        Gson gson = new Gson();
        String userJsonString = gson.toJson(user);
        response.getWriter().write(userJsonString);
    }

    protected void jQueryAjaxSerialize(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("jQueryAjaxSerialize 请求发过来了");

        // 获取客户端的请求
        String username = request.getParameter("username");
        System.out.println(username);
        String password = request.getParameter("password");
        System.out.println(password);

        // 将数据返回给客户端
        User user = new User(1, "潘K", "123456", "parzulpan@321.com");
        Gson gson = new Gson();
        String userJsonString = gson.toJson(user);
        response.getWriter().write(userJsonString);
    }

}
