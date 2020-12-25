package cn.parzulpan.dao;

import cn.parzulpan.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用户持久层接口
 */

@Repository
public interface UserDAO {
    /**
     * 查询所有用户信息
     * @return
     */
    @Select("select * from user")
    public List<User> findAll();

    /**
     * 根据 Id 查询用户信息
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    public User findById(Integer id);

    /**
     * 根据 用户名 查询用户信息
     * @param username
     * @return
     */
    @Select("select * from user where username = #{username}")
    public User findByName(String username);

    /**
     * 根据用户名和密码查询用户信息
     * @param username
     * @param password
     * @return
     */
    @Select("select * from user where username = #{username} and password = #{password}")
    public User findByNameAndPwd(String username, String password);

    /**
     * 保存用户
     * @param user
     * @return
     */
    @Insert("insert into user(username, password) values (#{username}, #{password})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = Integer.class, before = false,
            statement = {"select last_insert_id()"})
    public int save(User user);
}
