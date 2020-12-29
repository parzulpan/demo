package cn.parzulpan.controller;

import cn.parzulpan.dao.DepartmentDao;
import cn.parzulpan.dao.EmployeeDao;
import cn.parzulpan.entities.Department;
import cn.parzulpan.entities.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 员工管理控制器
 */

@Controller
public class EmployeeController {
    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    DepartmentDao departmentDao;

    /**
     * 查询所有员工，返回列表页面
     */
    @GetMapping("/emps")
    public String list(Model model) {
        Collection<Employee> employees = employeeDao.getAll();
        model.addAttribute("emps", employees);  // 结果放到请求域中
        return "emp/list";  // Thymeleaf 会自动拼串，classpath:/templates/emp/list.html
    }

    /**
     * 来到员工添加页面
     */
    @GetMapping("/emp")
    public String toAddPage(Model model) {
        // 查询所有部门，在页面显示
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts", departments);
        return "emp/add";
    }

    /**
     * 员工添加，SpringMVC 会自动进行参数绑定
     */
    @PostMapping("/emp")
    public String addEmp(Employee employee) {
        employeeDao.save(employee);
        return "redirect:/emps";
    }

    /**
     * 来到修改页面，查出当前员工，在页面回显
     */
    @GetMapping("/emp/{id}")
    public String toEditPage(@PathVariable("id") Integer id, Model model) {
        Employee employee = employeeDao.get(id);
        model.addAttribute("emp", employee);
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("depts", departments);
        return "emp/add";   // add.html 是一个修改和添加二合一的页面
    }

    /**
     * 员工修改，SpringMVC 会自动进行参数绑定
     */
    @PutMapping("/emp")
    public String updateEmp(Employee employee) {
        employeeDao.save(employee);
        return "redirect:/emps";
    }

    /**
     * 员工删除
     */
    @DeleteMapping("/emp/{id}")
    public String deleteEmp(@PathVariable("id") Integer id) {
        employeeDao.delete(id);
        return "redirect:/emps";
    }
}
