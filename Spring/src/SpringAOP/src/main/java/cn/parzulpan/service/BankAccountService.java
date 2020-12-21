package cn.parzulpan.service;

import cn.parzulpan.domain.BankAccount;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 银行账户的业务层接口
 */

public interface BankAccountService {

    /**
     * 查询所有银行账户
     * @return
     */
    List<BankAccount> findAll();

    /**
     * 根据账户 ID 查询一个账户
     * @param id
     * @return
     */
    BankAccount findById(Integer id);

    /**
     * 保存一个账户
     * @param bankAccount
     */
    void save(BankAccount bankAccount);

    /**
     * 更新一个账户
     * @param bankAccount
     */
    void update(BankAccount bankAccount);

    /**
     * 根据账户 ID 删除一个账户
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 一个账户向另外一个账户转钱
     * @param sourceName
     * @param targetName
     * @param money
     */
    void transfer(String sourceName, String targetName, Double money);
}
