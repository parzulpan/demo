package cn.parzulpan.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 自定义类型转换器的控制器
 */

@Controller
@RequestMapping("/convert")
public class CovertController {

    @RequestMapping(value = "/stringToDate")
    public String stringToDate(Date date) {
        System.out.println(date);
        return "hello";
    }
}
