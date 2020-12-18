package cn.parzulpan.dao;

import cn.parzulpan.domain.User;

import java.util.List;

/**
 * @Author : cn.parzulpan
 * @Time : 2020-12
 * @Desc :
 */

public interface UserDAO {

    /**
     * 查询所有信息
     * @return
     */
    List<User> findAll();

    /**
     * 根据 ID 查询操作
     * @param userId
     * @return
     */
    User findById(Integer userId);

    /**
     * 用户保存操作
     * @param user
     * @return 影响数据库记录的条数
     */
    int saveUser(User user);

    /**
     * 用户更新操作
     * @param user
     * @return 影响数据库记录的条数
     */
    int updateUser(User user);

    /**
     * 用户删除操作
     * @param userId
     * @return 影响数据库记录的条数
     */
    int deleteUser(Integer userId);

    /**
     * 用户模糊查询操作
     * @param username
     * @return
     */
    List<User> findByName(String username);

    /**
     * 用户模糊查询操作
     * @param username
     * @return
     */
    List<User> findByNameV2(String username);

    /**
     * 使用聚合函数查询
     * @return
     */
    int findTotal();
}
