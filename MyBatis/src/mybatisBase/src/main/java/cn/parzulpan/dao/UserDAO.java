package cn.parzulpan.dao;

import cn.parzulpan.domain.User;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12-15
 * @Desc :
 */

public interface UserDAO {

    /**
     * 查询所有信息
     * @return
     */
    List<User> findAll();
}
