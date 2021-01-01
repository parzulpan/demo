package cn.parzulpan.service;

import cn.parzulpan.bean.Department;
import cn.parzulpan.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc : 部门业务层
 */

@Service
public class DepartmentService {

    @Autowired
    DepartmentMapper departmentMapper;

    @Autowired
    RedisCacheManager departmentCacheManager;

    // 注解的方式
    @Cacheable(cacheNames = "dept", cacheManager = "departmentCacheManager")
    public Department getDeptById(Integer id) {
        return departmentMapper.getDeptById(id);
    }

    // api 调用的方式
    public Department getDeptById2(Integer id) {
        Department department = departmentMapper.getDeptById(id);

        // 获取某个缓存
        Cache dept = departmentCacheManager.getCache("dept");
        dept.put("dept2:" + id, department);

        return department;
    }
}
