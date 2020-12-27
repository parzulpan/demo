package cn.parzulpan.helloworld.cotroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

// 由于方法都是 Restful 风格，还可以直接用在类上
//@ResponseBody
//@Controller
// RestController 更加简单
@RestController
public class HelloController {

    // ResponseBody 将这个方法返回的数据直接写给浏览器，如果是对象还可以直接转为 json 数据
//    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        return "hello world quick!";
    }
}
