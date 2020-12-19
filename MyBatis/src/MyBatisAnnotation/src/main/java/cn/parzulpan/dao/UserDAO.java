package cn.parzulpan.dao;

import cn.parzulpan.domain.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户的持久层接口，使用注解开发
 */

@CacheNamespace(blocking = true)    // 基于注解方式实现配置二级缓存
public interface UserDAO {

    /**
     * 查询所有用户
     * @return
     */
    @Select("select * from user")
    @Results(id = "userMap",
            value = {
            @Result(id = true, column = "id", property = "userId"),
            @Result(column = "username", property = "userName"),
            @Result(column = "birthday", property = "userBirthday"),
            @Result(column = "sex", property = "userSex"),
            @Result(column = "address", property = "userAddress")
            })
    List<User> findAll();

    /**
     * 根据 id 查询一个用户
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{uid}")
    @ResultMap(value = {"userMap"})
    User findById(Integer userId);

    /**
     * 插入操作
     * @param user
     * @return
     */
    @Insert("insert into user(username, birthday, sex, address) values (#{userName}, #{userBirthday}, #{userSex}, #{userAddress})")
    @SelectKey(keyColumn = "id", keyProperty = "userId", resultType = Integer.class, before = false,
            statement = {"select last_insert_id()"})
    int saveUser(User user);

    /**
     * 更新操作
     * @param user
     * @return
     */
    @Update("update user set username = #{userName}, birthday = #{userBirthday}, sex = #{userSex}, " +
            "address = #{userAddress} where id = #{userId}")
    int updateUser(User user);

    /**
     * 删除操作
     * @param userId
     * @return
     */
    @Delete("delete from user where id = #{uid}")
    int deleteUser(Integer userId);

    /**
     * 使用聚合函数查询
     * @return
     */
    @Select("select count(*) from user")
    int findTotal();

    /**
     *
     * @param name
     * @return
     */
    @Select("select * from user where username like #{username}")
    @ResultMap(value = {"userMap"})
    List<User> findByName(String name);

    /**
     * 查询所有用户，包括账户列表
     * @return
     */
    @Select("select * from user")
    @Results(id = "userMapWithAccount",
            value = {
                    @Result(id = true, column = "id", property = "userId"),
                    @Result(column = "username", property = "userName"),
                    @Result(column = "birthday", property = "userBirthday"),
                    @Result(column = "sex", property = "userSex"),
                    @Result(column = "address", property = "userAddress"),
                    @Result(column = "id", property = "accounts", many = @Many(
                            select = "cn.parzulpan.dao.AccountDAO.findByUid",
                            fetchType = FetchType.LAZY
                    ))
            })
    List<User> findAllWithAccount();

}
