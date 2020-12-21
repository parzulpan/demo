package cn.parzulpan.dao;

import org.springframework.stereotype.Repository;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@Repository
public class AccountDAOImpl2 implements AccountDAO {
    @Override
    public void saveAccount() {
        System.out.println("222 账户保存成功...");
    }
}
