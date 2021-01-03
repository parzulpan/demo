package cn.parzulpan.ticket.service;


import org.springframework.stereotype.Service;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc :
 */

@Service
@com.alibaba.dubbo.config.annotation.Service    //将服务发布出去
public class TicketServiceImpl implements TicketService {
    @Override
    public String getTicket() {
        return "《大闹天宫》";
    }
}
