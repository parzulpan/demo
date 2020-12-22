package cn.parzulpan.service;

import cn.parzulpan.domain.Account;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户的业务层接口
 */

public interface AccountService {

    /**
     * 根据 Id 查询账户信息
     * @param accountId
     * @return
     */
    Account findById(Integer accountId);

    /**
     * 转账
     * @param sourceName
     * @param targetName
     * @param money
     */
    void transfer(String sourceName, String targetName, Double money);
}
