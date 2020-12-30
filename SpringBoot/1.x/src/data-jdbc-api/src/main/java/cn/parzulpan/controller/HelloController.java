package cn.parzulpan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@Controller
public class HelloController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @ResponseBody
    @GetMapping("/query")
    public Map<String, Object> query() {
        List<Map<String, Object>> lists = jdbcTemplate.queryForList("select * from department");
        return lists.get(0);
    }

}
