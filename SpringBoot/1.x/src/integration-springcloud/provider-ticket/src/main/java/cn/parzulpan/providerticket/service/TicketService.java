package cn.parzulpan.providerticket.service;

import org.springframework.stereotype.Service;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc :
 */

@Service
public class TicketService {

    public String getTicket() {
        System.out.println("8002");
        return "《流浪地球》";
    }
}
