package cn.parzulpan.service.impl;

import cn.parzulpan.dao.ProductDao;
import cn.parzulpan.domain.Product;
import cn.parzulpan.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 产品业务层接口的实现类
 */

@Service("productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }
}
