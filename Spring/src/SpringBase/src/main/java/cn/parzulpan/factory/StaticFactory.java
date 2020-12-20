package cn.parzulpan.factory;

import cn.parzulpan.service.AccountService;
import cn.parzulpan.service.AccountServiceImplIOC;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : Spring 管理静态工厂。模拟一个工厂类，该类可能存在于 jar 包中，无法通过修改源码来提供默认构造函数
 */

public class StaticFactory {
    public static AccountService getAccountService() {
        return new AccountServiceImplIOC();
    }
}
