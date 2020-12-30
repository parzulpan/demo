package cn.parzulpan.controller;

import cn.parzulpan.starter.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@RestController
public class HelloController {

    @Autowired
    HelloService helloService;

    // http://localhost:8080/hello
    @GetMapping("/hello")
    public String hello() {
        return helloService.sayHelloName("curry");
    }

}
