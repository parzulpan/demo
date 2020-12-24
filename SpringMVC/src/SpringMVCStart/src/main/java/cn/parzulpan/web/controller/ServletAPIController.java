package cn.parzulpan.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 原始 ServletAPI 作为控制器参数
 */

@Controller
@RequestMapping("/servletAPI")
public class ServletAPIController {

    @RequestMapping("/testServletAPI")
    public String testServletAPI(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        System.out.println(request);
        System.out.println(response);
        System.out.println(session);
        return "world";
    }
}
