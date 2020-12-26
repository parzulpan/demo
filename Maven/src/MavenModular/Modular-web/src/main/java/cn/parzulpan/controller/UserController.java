package cn.parzulpan.controller;

import cn.parzulpan.domain.User;
import cn.parzulpan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户控制器
 */

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param user
     * @return
     */
    @RequestMapping("/login")
    public String login(User user){
        System.out.println("表现层：登录用户");
        System.out.println(user);

        User userByNameAndPwd = userService.findUserByNameAndPwd(user.getUsername(), user.getPassword());
        if (userByNameAndPwd != null) {
            System.out.println("登录成功");
            return "redirect:findAll";
        }

        return "error";
    }

    /**
     * 注册
     * @param user
     * @return
     */
    @RequestMapping("/registration")
    public String registration(User user){
        System.out.println("表现层：注册用户");
        System.out.println(user);

        int i = userService.saveUser(user);
        if (i > 0) {
            System.out.println("注册成功");
            return "redirect:findAll";
        }

        return "error";
    }

    /**
     * 查询所有账户
     * @param model
     * @return
     */
    @RequestMapping("/findAll")
    public String findAll(Model model) {
        System.out.println("表现层：查询所有账户");

        List<User> users = userService.findAllUser();
        model.addAttribute("users", users);

        return "success";
    }


    /**
     * 返回首页
     * @return
     */
    @RequestMapping("/returnIndex")
    public String returnIndex() {
        System.out.println("表现层：返回首页");

        return "redirect:/index.jsp";
    }
}
