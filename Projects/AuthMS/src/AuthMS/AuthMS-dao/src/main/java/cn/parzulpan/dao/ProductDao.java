package cn.parzulpan.dao;

import cn.parzulpan.domain.Product;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 产品持久层接口
 */

@Repository
public interface ProductDao {

    /**
     * 查询所有产品
     */
    @Select("select * from product")
    List<Product> findAll();
}
