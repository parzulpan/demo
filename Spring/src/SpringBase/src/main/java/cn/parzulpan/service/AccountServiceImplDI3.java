package cn.parzulpan.service;

import java.util.*;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 账户业务层接口的实现类，使用 set 方法依赖注入，注入集合属性
 */

public class AccountServiceImplDI3 implements AccountService{
//    private AccountDAO accountDAO = new AccountDAOImpl();   // 这里发生了耦合
    private String[] myStr;
    private List<String> myList;
    private Set<String> mySet;
    private Map<String,String> myMap;
    private Properties myProps;

    public void setMyStr(String[] myStr) {
        this.myStr = myStr;
    }

    public void setMyList(List<String> myList) {
        this.myList = myList;
    }

    public void setMySet(Set<String> mySet) {
        this.mySet = mySet;
    }

    public void setMyMap(Map<String, String> myMap) {
        this.myMap = myMap;
    }

    public void setMyProps(Properties myProps) {
        this.myProps = myProps;
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
        System.out.println(Arrays.toString(myStr));
        System.out.println(myList);
        System.out.println(mySet);
        System.out.println(myMap);
        System.out.println(myProps);
    }

    public void init() {
        System.out.println("AccountService 对象初始化...");
    }

    public void destroy() {
        System.out.println("AccountService 对象销毁...");
    }
}
