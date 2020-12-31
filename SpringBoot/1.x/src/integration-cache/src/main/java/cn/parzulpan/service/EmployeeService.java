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
 * @Desc :
 */

@CacheConfig(cacheNames = {"emp"})    // 指定这个类的缓存配置，通常用于抽取公共配置
@Service
public class EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;

    /**
     * @Cacheable 能够根据方法的请求参数对其结果进行缓存
     * 参数：
     *   cacheNames/value: 缓存的名字
     *   key: 缓存的数据，为空时默认是使用方法参数的值，可以为 SpEL 表达式，例如 #id
     *   keyGenerator: key 的生成器，可以自己指定 key 的生成器的组件 id，它与 key 二选一
     *   cacheManager: 缓存管理器
     *   cacheResolver: 缓存解析器，它与 cacheManager 二选一
     *   condition: 执行符合条件才缓存
     *   unless:  执行不符合条件才缓存
     *   sync: 是否使用异步模式
     *
     * 原理：
     *   自动配置类 CacheAutoConfiguration
     *   缓存配置类
     */
    @Cacheable(cacheNames = {"emp"})
    public Employee getEmp(Integer id) {
        return employeeMapper.getEmpById(id);
    }

    /**
     * @CachePut 即调用方法，又更新缓存数据
     */
    @CachePut(value = {"emp"})
    public Employee updateEmp(Employee employee) {
        employeeMapper.updateEmp(employee);
        return  employee;
    }

    /**
     * @CacheEvict 缓存清除
     * 参数：
     *   key: 指定要清除的数据
     *   allEntries: 指定清除这个缓存中的所有数据
     *   beforeInvocation: 缓存的清除是否在方法之前执行，默认是缓存清除操作是在方法之后执行，出现异常不会清除缓存
     */
    @CacheEvict(value = {"emp"})
    public void deleteEmp(Integer id) {
        employeeMapper.deleteEmpById(id);
    }

    /**
     * @Caching 定义复杂的缓存规则
     */
    @Caching(
            cacheable = {
                    @Cacheable(value = {"emp"}, key = "#lastName")
            },
            put = {
                    @CachePut(value = {"emp"}, key = "#result.id"),
                    @CachePut(value = {"emp"}, key = "#result.email")
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
