package cn.parzulpan.factory;

import cn.parzulpan.service.BankAccountService;
import cn.parzulpan.utils.ConnectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用于创建 业务层实现类 的 代理对象工厂
 */

@Component
public class BeanFactory {
    @Autowired
    private BankAccountService bankAccountService;  // 被代理类
    @Autowired
    private ConnectionUtil connectionUtil;

    /**
     * 获取 业务层实现类 的 代理对象
     * @return
     */
    public BankAccountService getBankAccountService() {
        System.out.println("获取 业务层实现类 的 代理对象");
        return (BankAccountService) Proxy.newProxyInstance(bankAccountService.getClass().getClassLoader(),
                bankAccountService.getClass().getInterfaces(),
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 添加事务控制
                        Object rtValue = null;
                        try {
//                            accounts =  bankAccountDAO.findAll();
                            rtValue = method.invoke(bankAccountService, args);
                            connectionUtil.commitAndClose();
                        } catch (Exception e) {
                            connectionUtil.rollbackAndClose();
                            throw new RuntimeException(e);
                        }
                        return rtValue;
                    }
                });
    }
}
