package cn.parzulpan.dao;

import cn.parzulpan.domain.Account;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public interface AccountDAO {

    /**
     * 查询所有账户，同时获取账户的所属用户名称以及它的地址信息
     * @return
     */
    List<Account> findAll();

    /**
     * 查询所有账户，懒加载
     * @return
     */
    List<Account> findAllLazy();
}
