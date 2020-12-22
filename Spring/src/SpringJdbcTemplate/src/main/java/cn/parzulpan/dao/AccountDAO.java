package cn.parzulpan.dao;

import domain.Account;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户持久层接口
 */

public interface AccountDAO {

    List<Account> findAll();

    Account findById(Integer id);

    Account findByName(String name);

    void update(Account account);

    void insert(Account account);

    void delete(Integer id);

    Long getCount();
}
