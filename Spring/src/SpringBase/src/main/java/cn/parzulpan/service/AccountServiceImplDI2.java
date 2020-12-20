package cn.parzulpan.service;

import java.util.Date;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户业务层接口的实现类，使用 set 方法依赖注入
 */

public class AccountServiceImplDI2 implements AccountService{
//    private AccountDAO accountDAO = new AccountDAOImpl();   // 这里发生了耦合
    private String name;    // 如果是经常变变化的数据，并不适用于依赖注入
    private Integer age;
    private Date birthday;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * 模拟保存账户
     */
    public void saveAccount() {
//        AccountDAO accountDAO = (AccountDAO) BeanFactory.getBean("accountDAO"); // 通过 Factory 解耦
//        if (accountDAO != null) {
//            accountDAO.saveAccount();
//        }
        System.out.println("call saveAccount() ");
        System.out.println("call saveAccount() " + name + " " + age + " " + birthday);
    }

    public void init() {
        System.out.println("AccountService 对象初始化...");
    }

    public void destroy() {
        System.out.println("AccountService 对象销毁...");
    }
}
