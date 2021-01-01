package cn.parzulpan.controller;

import cn.parzulpan.bean.Department;
import cn.parzulpan.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc : 部门控制器
 */

@RestController
public class DepartmentController {

    @Autowired
    DepartmentService departmentService;

    //
    @GetMapping("/dept/{id}")
    public Department getDept(@PathVariable("id") Integer id) {
        return departmentService.getDeptById(id);
    }

    //
    @GetMapping("/dept2/{id}")
    public Department getDept2(@PathVariable("id") Integer id) {
        return departmentService.getDeptById2(id);
    }
}
