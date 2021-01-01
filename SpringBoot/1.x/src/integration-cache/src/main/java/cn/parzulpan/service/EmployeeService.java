package cn.parzulpan.service;

import cn.parzulpan.bean.Employee;
import cn.parzulpan.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 员工业务层
 */

@CacheConfig(cacheNames = {"emp"}, cacheManager = "employeeCacheManager")    // 指定这个类的缓存配置，通常用于抽取公共配置
@Service
public class EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;

    /**
     *
     * 根据 Id 查询员工信息
     *
     * @Cacheable 运行流程：
     * 1. 方法运行之前，先去查询 Cache 缓存组件，按照 cacheNames 指定的名字获取，第一次获取缓存时如果没有 Cache 组件会自动创建
     * 2. 去 Cache 中查询缓存的内容，使用一个 key，默认是方法的参数。也可以按照某种策略生成，默认使用 SimpleKeyGenerator 生成 key
     *    SimpleKeyGenerator 生成 key 的默认策略为：
     *      如果没有参数，key = new SimpleKey()
     *      如果有一个参数，key = 参数的值
     *      如果有多个参数，key = new SimpleKey(params)
     * 3. 有查询到缓存，则直接使用缓存；没有查询到缓存，则调用目标方法并将目标方法返回的结果放进缓存中
     */
    @Cacheable(/*cacheNames = {"emp"}*/)
    public Employee getEmp(Integer id) {
        return employeeMapper.getEmpById(id);
    }

    /**
     *
     * 更新员工信息
     *
     * @CachePut 运行流程
     * 1. 先调用目标方法
     * 2. 将目标方法的结果缓存起来
     * 3. 比较适用与修改了数据库某个数据后，更新缓存
     */
    @CachePut(/*value = {"emp"}, key = "#result.id", */keyGenerator = "customKeyGenerator")
    public Employee updateEmp(Employee employee) {
        employeeMapper.updateEmp(employee);
        return  employee;
    }

    /**
     *
     * 删除员工信息
     *
     */
    @CacheEvict(/*value = {"emp"}, */key = "#id")
    public void deleteEmp(Integer id) {
        employeeMapper.deleteEmpById(id);
    }

    /**
     *
     * 根据 lastName 查询员工信息
     *
     * @Caching 定义复杂的缓存规则
     */
    @Caching(
            cacheable = {
                    @Cacheable(/*value = {"emp"}, */key = "#lastName")
            },
            put = {
                    @CachePut(/*value = {"emp"}, */key = "#result.id"),
                    @CachePut(/*value = {"emp"}, */key = "#result.email")
            }
    )
    public Employee getEmp(String lastName) {
        List<Employee> employees = employeeMapper.getEmpByName(lastName);
        if (employees.isEmpty()) {
            return null;
        }
        return employees.get(0);
    }
}
