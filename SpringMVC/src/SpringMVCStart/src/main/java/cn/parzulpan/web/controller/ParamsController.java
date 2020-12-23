package cn.parzulpan.web.controller;

import cn.parzulpan.domain.Account;
import cn.parzulpan.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 请求参数绑定的控制器
 */

@Controller
@RequestMapping(path = "/params")
public class ParamsController {

    /**
     * 基本数据类型和字符串类型
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/stringAndIntegerParams")
    public String stringAndIntegerParams(String username, Integer password) {
        System.out.println(username);
        System.out.println(password);
        return "hello";
    }

    /**
     * 实体类型（JavaBean）
     * @param account
     * @return
     */
    @RequestMapping("/javaBeanParams")
    public String javaBeanParams(Account account) {
        System.out.println(account);
        return "hello";
    }

    /**
     * 集合数据类型（List、Map等）
     * @param user
     * @return
     */
    @RequestMapping("/collectionParams")
    public String collectionParams(User user) {
        System.out.println(user);
        return "hello";
    }
}
