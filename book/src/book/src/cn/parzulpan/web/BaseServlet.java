package cn.parzulpan.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @Author : parzulpan
 * @Time : 2020-12-10
 * @Desc : BaseServlet 程序
 */

@WebServlet(name = "BaseServlet")
public abstract class BaseServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String action = request.getParameter("action");
//
//        // 使用反射优化，省去大量的判断代码
//        try {
//            // 获取 action 业务鉴别字符串，获得相应的业务员方法反射对象
//            Method method = this.getClass().getDeclaredMethod(action, HttpServletRequest.class, HttpServletResponse.class);
//            method.invoke(this, request, response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        System.out.println("doPost");
        doAction(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("doGet");
        doAction(request, response);
    }

    // Post and Get Common
    protected void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 解决中文乱码
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String action = request.getParameter("action");

        // 使用反射优化，省去大量的判断代码
        try {
            // 获取 action 业务鉴别字符串，获得相应的业务员方法反射对象
            Method method = this.getClass().getDeclaredMethod(action, HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(this, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);// 把异常抛给事务过滤器
        }
    }
}
