package cn.parzulpan.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户登录控制器
 */

@Controller
public class LoginController {

    /**
     * 登录页面
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping("/login")
    public String login(Model model) throws Exception {
        System.out.println("called login...");

        return "login";
    }

    /**
     * 提交登录
     * @param session
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping("/loginSubmit")
    public String loginSubmit(HttpSession session, String username, String password) throws Exception {
        System.out.println("called loginSubmit...");
        System.out.println(username + " " + password);

        session.setAttribute("activeUser", username);   // 向 session 记录用户身份信息

        return "forward:/WEB-INF/views/main.jsp";
    }

    /**
     * 退出登录
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping("logout")
    public String logout(HttpSession session) throws Exception {
        System.out.println("called logout...");
        session.invalidate();   // 设置 session 过期

        return "redirect:/index.jsp";
    }

    /**
     * 测试页面
     * @return
     * @throws Exception
     */
    @RequestMapping("/test")
    public String test() throws Exception {
        System.out.println("called test...");

        return "test";
    }
}
