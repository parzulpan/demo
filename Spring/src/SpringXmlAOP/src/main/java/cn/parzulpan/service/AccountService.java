package cn.parzulpan.service;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户的业务层接口
 */

public interface AccountService {

    /**
     * 模拟保存账户
     */
    void saveAccount();

    /**
     * 模拟更新用户
     * @param id
     */
    void updateAccount(int id);

    /**
     * 模拟删除账户
     * @return
     */
    int deleteAccount();
}
