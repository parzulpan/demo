package cn.parzulpan.user.service;

import cn.parzulpan.ticket.service.TicketService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc :
 */

@Service
public class UserService {

    @Reference  // 远程引用，按照注册中心的全类名匹配的
    TicketService ticketService;

    public void get() {
        String ticket = ticketService.getTicket();
        System.out.println("买到票了：" + ticket);
    }
}
