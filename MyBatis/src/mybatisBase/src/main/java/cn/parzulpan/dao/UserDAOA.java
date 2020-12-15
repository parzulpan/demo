package cn.parzulpan.dao;

import cn.parzulpan.domain.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-15
 * @Desc : 基于注解的
 */

public interface UserDAOA {
    /**
     * 查询所有信息
     * @return
     */
    @Select("select * from user")
    List<User> findAll();
}
