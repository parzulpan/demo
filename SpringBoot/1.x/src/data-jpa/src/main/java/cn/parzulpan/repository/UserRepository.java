package cn.parzulpan.repository;

import cn.parzulpan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 操作实体类对应的数据表的接口
 * JpaRepository<T, ID extends Serializable>
 * T 是 实体类，ID 是实体类的主键
 */

public interface UserRepository extends JpaRepository<User, Integer> {
}
