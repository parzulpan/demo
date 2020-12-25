package cn.parzulpan.service.impl;

import cn.parzulpan.domain.User;
import cn.parzulpan.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 对 用户业务层接口的实现类 进行单元测试
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/applicationContext.xml"})
public class UserServiceImplTest {

    @Autowired
    private UserService us;

    @Test
    public void findAllUser() {
        List<User> users = us.findAllUser();
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void findUserById() {
        User user = us.findUserById(1);
    }

    @Test
    public void findUserByName() {
        User user = us.findUserByName("admin");
    }

    @Test
    public void saveUser() {
        us.saveUser(new User(null, "test", "test1234"));
    }
}