package cn.parzulpan.repository;

import cn.parzulpan.bean.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc : 操作 ES
 */

public interface BookRepository extends ElasticsearchRepository<Book, Integer> {

    // 更多可参考：https://docs.spring.io/spring-data/elasticsearch/docs/2.1.23.RELEASE/reference/html/#reference
    public List<Book> findByBookNameLike(String bookName);  // 自定义方法，按书名模糊查询
}
