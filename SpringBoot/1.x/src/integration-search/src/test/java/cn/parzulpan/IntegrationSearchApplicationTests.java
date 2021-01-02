package cn.parzulpan;


import cn.parzulpan.bean.Article;
import cn.parzulpan.bean.Book;
import cn.parzulpan.repository.BookRepository;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrationSearchApplicationTests {

    @Autowired
    JestClient jestClient;  // Jest 操作 ES

    @Autowired
    BookRepository bookRepository;  // 编写 ElasticsearchRepository 的子接口 来 操作 ES

    // 创建一个索引 http://localhost:9200/parzulpan/tips/1001
    @Test
    public void testJestCreate() {
        // 给 ES 中 索引（保存）一个文档
        Article article = new Article(1001, "消息通知", "zs", "Hello World");

        // 构建一个索引
        Index index = new Index.Builder(article).index("parzulpan").type("tips").build();

        try {
            // 执行
            DocumentResult result = jestClient.execute(index);
            System.out.println(result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 全文搜索
    @Test
    public void testJestSearch() {
        // 全文搜索 查询表达式
        String json = "{\n" +
                "    \"query\" : {\n" +
                "        \"match\" : {\n" +
                "            \"content\" : \"Hello\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        // 构建一个搜索
        Search search = new Search.Builder(json).addIndex("parzulpan").addType("tips").build();

        try {
            // 执行
            SearchResult result = jestClient.execute(search);
            System.out.println(result.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // http://localhost:9201/parzulpan/book/1
    @Test
    public void testElasticsearchRepositoryCreate() {
        Book book = new Book(1, "Elasticsearch 实战", "parzulpan");
        Book index = bookRepository.index(book);
        System.out.println(index);
    }

    @Test
    public void testElasticsearchRepositorySearch() {
        List<Book> books = bookRepository.findByBookNameLike("实战");
        System.out.println(books);
    }
}
