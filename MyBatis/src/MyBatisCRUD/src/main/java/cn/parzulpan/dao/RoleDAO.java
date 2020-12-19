package cn.parzulpan.dao;

import cn.parzulpan.domain.Role;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public interface RoleDAO {

    /**
     * 查询所有角色
     * @return
     */
    List<Role> findAll();
}
