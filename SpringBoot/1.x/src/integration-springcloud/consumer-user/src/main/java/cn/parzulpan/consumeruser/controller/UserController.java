package cn.parzulpan.consumeruser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc :
 */

@RestController
public class UserController {

    @Autowired
    RestTemplate restTemplate;

    // http://192.168.0.100:8200/buy?name=parzulpan
    @GetMapping("/buy")
    public String buyTicket(String name) {
        String s = restTemplate.getForObject("http://PROVIDER-TICKET/ticket", String.class);    // 消费服务
        return name + " 购买了 " + s;
    }
}
