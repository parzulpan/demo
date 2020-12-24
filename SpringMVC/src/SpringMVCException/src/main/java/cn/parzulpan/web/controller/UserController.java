package cn.parzulpan.web.controller;

import cn.parzulpan.exception.CustomException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/testException")
    public String testException() throws CustomException {
        System.out.println("called testException...");

        try {
            int a = 10 / 0; //模拟异常
        } catch (Exception e) {
            throw new CustomException("执行用户查询时出错了...");
        }

        return "success";
    }
}
