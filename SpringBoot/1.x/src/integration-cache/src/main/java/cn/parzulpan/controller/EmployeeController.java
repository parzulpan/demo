package cn.parzulpan.controller;

import cn.parzulpan.bean.Employee;
import cn.parzulpan.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 员工控制器
 */

@RestController
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    // http://localhost:8080/emp/1
    @GetMapping("/emp/{id}")
    public Employee getEmp(@PathVariable("id") Integer id) {
        return employeeService.getEmp(id);
    }

    // http://localhost:8080/emp?id=1&lastName=ha&email=ha@gmail.com&gender=0&dId=1001
    @GetMapping("/emp")
    public Employee updateEmp(Employee employee) {
        return employeeService.updateEmp(employee);
    }

    // http://localhost:8080/empDel?id=1
    @GetMapping("/empDel")
    public String deleteEmp(Integer id) {
        employeeService.deleteEmp(id);
        return "success";
    }

    // http://localhost:8080/emp/lastName/parzulpan
    @GetMapping("/emp/lastName/{lastName}")
    public Employee getEmp(@PathVariable("lastName") String lastName) {
        return employeeService.getEmp(lastName);
    }
}
