package cn.parzulpan.service;

import cn.parzulpan.domain.User;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户业务层接口
 */

public interface UserService {

    /**
     * 查询所有用户信息
     * @return
     */
    public List<User> findAllUser();

    /**
     * 根据 Id 查询用户信息
     * @param id
     * @return
     */
    public User findUserById(Integer id);

    /**
     * 根据 用户名 查询用户信息
     * @param username
     * @return
     */
    public User findUserByName(String username);

    /**
     * 根据用户名和密码查询用户信息
     * @param username
     * @param password
     * @return
     */
    public User findUserByNameAndPwd(String username, String password);

    /**
     * 保存用户
     * @param user
     * @return
     */
    public int saveUser(User user);
}
