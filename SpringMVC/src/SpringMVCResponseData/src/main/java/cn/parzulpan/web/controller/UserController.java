package cn.parzulpan.web.controller;

import cn.parzulpan.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@Controller
@RequestMapping(path = {"/user"})
public class UserController {

    /**
     * 模拟异步请求响应
     * @return
     */
    @RequestMapping(path = {"/testAjax"})
    public @ResponseBody User testAjax(@RequestBody User user) {
        System.out.println("called testAjax...");
        System.out.println(user);
        user.setPassword("Mparzulpan0101");
        return user;
    }
}
