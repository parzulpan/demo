package cn.parzulpan.service;

import cn.parzulpan.bean.Book;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc : 数据业务类
 */

@Service
public class BookService {

    @RabbitListener(queues = {"parzulpan", "parzulpan.emps"})
    public void receive(Book book) {
        System.out.println("收到消息: " + book);
    }
}
