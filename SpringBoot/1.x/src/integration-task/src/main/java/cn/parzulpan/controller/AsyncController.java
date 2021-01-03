package cn.parzulpan.controller;

import cn.parzulpan.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc :
 */

@RestController
public class AsyncController {

    @Autowired
    AsyncService asyncService;

    // http://localhost:8080/hello
    @GetMapping("/hello")
    public String hello() {
        asyncService.hello();
        return "success";
    }
}
