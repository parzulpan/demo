package cn.parzulpan.service;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户的业务层接口的实现类
 */

public class AccountServiceImpl implements AccountService {
    public void saveAccount() {
        System.out.println("执行了保存操作...");
    }

    public void updateAccount(int id) {
        System.out.println("执行了更新操作... " + id);
    }

    public int deleteAccount() {
        System.out.println("执行了删除操作...");
        return 0;
    }
}
