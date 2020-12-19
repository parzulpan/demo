package cn.parzulpan.dao;

import cn.parzulpan.domain.QueryV;
import cn.parzulpan.domain.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author : parzulpan
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

    /**
     * 根据 QueryV 中的条件查询用户
     * @param v
     * @return
     */
    List<User> findByQueryV(QueryV v);

    /**
     * 根据用户信息，查询用户列表
     * @param user
     * @return
     */
    List<User> findByUser(User user);

    /***
     * 根据用户信息，查询用户列表，提供默认情况
     * @param user
     * @return
     */
    List<User> findByUserDefault(User user);

    /**
     * 根据用户信息，查询用户列表，使用 Where
     * @param user
     * @return
     */
    List<User> findByUserWhere(User user);

    /**
     * 根据 id 集合查询用户
     * @param v
     * @return
     */
    List<User> findByIds(QueryV v);

    /**
     * 查询所有用户，同时获取出每个用户下的所有账户信息
     * @return
     */
    List<User> findAllAndAccount();

    /**
     * 查询所有信息，使用懒加载
     * @return
     */
    List<User> findByIdLazy();

    /**
     * 根据 ID 查询操作，使用一级缓存
     * @param id
     * @return
     */
    User findByIdCache(Integer id);

    /**
     * 根据 ID 查询操作，使用二级缓存
     * @param id
     * @return
     */
    User findByIdHighCache(Integer id);
}
