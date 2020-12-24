package cn.parzulpan.web.controller;

import cn.parzulpan.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@Controller
@RequestMapping(path = {"/return"})
public class ReturnController {

    @RequestMapping(path = {"/testReturnString"})
    public String testReturnString(Model model) {
        System.out.println("testReturnString");

        // 模拟从数据库中查询的数据
        User user = new User();
        user.setUsername("张三");
        user.setPassword("123");
        model.addAttribute("user", user);
        return "update";
    }

    @RequestMapping(path = {"/testReturnVoid"})
    public void testReturnVoid(HttpServletRequest request, HttpServletResponse response) throws Exception{
        System.out.println("testReturnVoid");

        // 使用 request 请求转发
        request.getRequestDispatcher("/WEB-INF/views/update.jsp").forward(request, response);

        // 使用 response 重定向
        response.sendRedirect("testReturnString");

        // 使用 response 指定响应结果
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("name: value");
    }

    @RequestMapping(path = {"testReturnModelAndView"})
    public ModelAndView testReturnModelAndView() {
        System.out.println("testReturnModelAndView");

        ModelAndView mv = new ModelAndView();
        mv.setViewName("success");  // // 跳转到 success.jsp 的页面

        // 模拟从数据库中查询所有的用户信息
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("张三");
        user1.setPassword("123");
        User user2 = new User();
        user2.setUsername("赵四");
        user2.setPassword("456");
        users.add(user1);
        users.add(user2);

        mv.addObject("users", users);

        return mv;
    }
}
