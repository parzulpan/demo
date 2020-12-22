package cn.parzulpan.dao;

import cn.parzulpan.domain.Account;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户的持久层接口
 */

public interface AccountDAO {

    Account findById(Integer accountId);

    Account findByName(String name);

    void update(Account account);
}
