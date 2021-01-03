package cn.parzulpan.providerticket.controller;

import cn.parzulpan.providerticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc :
 */

@RestController
public class TicketController {

    @Autowired
    TicketService ticketService;

    // http://192.168.0.100:8001/ticket
    @GetMapping("/ticket")
    public String getTicket() {
        return ticketService.getTicket();
    }
}
