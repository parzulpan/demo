package cn.parzulpan.service.impl;

import cn.parzulpan.dao.UserDAO;
import cn.parzulpan.domain.User;
import cn.parzulpan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户业务层接口的实现类
 */

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public List<User> findAllUser() {
        System.out.println("用户业务层：查询所有用户信息");
        return userDAO.findAll();
    }

    @Override
    public User findUserById(Integer id) {
        System.out.println("用户业务层：根据 Id 查询用户信息");
        return userDAO.findById(id);
    }

    @Override
    public User findUserByName(String username) {
        System.out.println("用户业务层：根据 用户名 查询用户信息");
        return userDAO.findByName(username);
    }

    @Override
    public User findUserByNameAndPwd(String username, String password) {
        System.out.println("用户业务层：根据 用户名 和 密码 查询用户信息");
        return userDAO.findByNameAndPwd(username, password);
    }

    @Override
    public int saveUser(User user) {
        System.out.println("用户业务层：保存用户");
        return userDAO.save(user);
    }
}
