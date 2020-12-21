package cn.parzulpan.service;


import cn.parzulpan.dao.AccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc :
 */

@Service
@Scope("singleton")
public class AccountServiceImpl implements AccountService {

//    @Autowired
//    private AccountDAO accountDAO;

//    @Autowired
//    @Qualifier("accountDAOImpl2")
//    private AccountDAO accountDAO;

    @Resource(name = "accountDAOImpl2")
    private AccountDAO accountDAO;

    @Override
    public void saveAccount() {
        System.out.println(accountDAO);
        accountDAO.saveAccount();
    }

    @PostConstruct
    public void init() {
        System.out.println("AccountService 对象初始化...");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("AccountService 对象销毁...");
    }
}
