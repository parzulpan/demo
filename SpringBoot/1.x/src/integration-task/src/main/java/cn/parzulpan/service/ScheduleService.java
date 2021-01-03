package cn.parzulpan.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @Author : parzulpan
 * @Time : 2021-01
 * @Desc :
 */

@Service
public class ScheduleService {

    /**
     * cron 表达式
     *
     * second(秒), minute（分）, hour（时）, day of month（日）, month（月）, day of week（周几）.
     * 示例：0 * * * * 0-6  代表从周日到周六，每分钟的第0秒执行
     *
     * 常用：
     *  【0 0/5 14,18 * * ?】 每天 14 点整，和 18 点整，每隔 5 分钟执行一次
     *  【0 15 10 ? * 1-6】 每个月的周一至周六 10:15 执行一次
     *  【0 0 2 ? * 6L】 每个月的最后一个周六凌晨 2:00 执行一次
     *  【0 0 2 LW * ?】 每个月的最后一个工作日凌晨 2:00 执行一次
     *  【0 0 2-4 ? * 1#1】 每个月的第一个周一凌晨2:00 到 4:00期间，每个整点都执行一次；
     */
    // @Scheduled(cron = "0 * * * * 0-6")
    // @Scheduled(cron = "0,1,2,3,4 * * * * 0-6")
    // @Scheduled(cron = "0-4 * * * * 0-6")
    @Scheduled(cron = "0/4 * * * * 0-6")  // 每 4 秒执行一次
    public void runHello() {
        System.out.println("runHello...");
    }
}
