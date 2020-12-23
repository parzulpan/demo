package cn.parzulpan.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 控制器，由于返回了结果，视图解析器会进行解析匹配，所以需要有对应的响应页面
 */

@Controller
@RequestMapping(path = "/say")
public class StartController {

    @RequestMapping(path = "/hello")
    public String sayHello() {
        System.out.println("SpringMVC Hello");
        return "hello";
    }

    @RequestMapping(path = "/world")
    public String sayWorld() {
        System.out.println("SpringMVC World");
        return "world";
    }
}
