package cn.parzulpan.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc :
 */

@Service
public class AsyncService {

    @Async  // 告诉 Spring 这是一个异步方法
    public void hello() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("数据处理中");
    }
}
