package cn.parzulpan.dao;


/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户持久层接口的实现类，使用 IOC
 */

public class AccountDAOImplIOC implements AccountDAO{
    /**
     * 模拟保存账户
     */
    public void saveAccount() {
        System.out.println("保存了账户...");
    }

    public void init() {
        System.out.println("AccountDAO 对象初始化...");
    }

    public void destroy() {
        System.out.println("AccountDAO 对象销毁...");
    }
}
