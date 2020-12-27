package cn.parzulpan.service;

import cn.parzulpan.domain.Product;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 产品业务层接口
 */

public interface ProductService {
    /**
     * 查询所有产品
     */
    public List<Product> findAll();
}
