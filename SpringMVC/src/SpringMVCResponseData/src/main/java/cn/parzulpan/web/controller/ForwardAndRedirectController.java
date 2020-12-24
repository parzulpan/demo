package cn.parzulpan.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@Controller
@RequestMapping(path = {"/"})
public class ForwardAndRedirectController {

    @RequestMapping(path = "/testForward")
    public String testForward() {
        System.out.println("请求转发");

        return "forward:/WEB-INF/views/success.jsp";
    }

    @RequestMapping(path = "/testRedirect")
    public String testRedirect() {
        System.out.println("请求重定向");

        return "redirect:/index.jsp";
    }
}
