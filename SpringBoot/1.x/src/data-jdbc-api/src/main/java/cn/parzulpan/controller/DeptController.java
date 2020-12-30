package cn.parzulpan.controller;

import cn.parzulpan.bean.Department;
import cn.parzulpan.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@RestController
public class DeptController {
    @Autowired
    DepartmentMapper departmentMapper;

    // http://localhost:8080/dept?departmentName=Admin
    @GetMapping("/dept")
    public Department addDepartment(Department department) {
        departmentMapper.insertDept(department);
        return department;
    }

    // http://localhost:8080/dept/1001
    @GetMapping("/dept/{id}")
    public Department getDepartment(@PathVariable("id") Integer id) {
        return departmentMapper.getDeptById(id);
    }

}
