package cn.parzulpan.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @Author : parzulpan
 * @Time : 2020-12
 * @Desc : 用于记录日志的工具类，它提供了公共的方法，即 Advice 通知，使用注解
 */

@Component
@Aspect
public class Logger {
    // 编写切入点表达式注解
    @Pointcut("execution(* cn.parzulpan.service.*.*(..))")
    private void allMethodPCRGlobal(){}

    /**
     * 打印日志
     * 前置通知，在 切入点方法（业务层中增强的方法）之前执行
     */
    @Before("allMethodPCRGlobal()")
    public void printLogBefore() {
        System.out.println("Logger 类中的 printLogBefore 方法开始记录日志了...");
    }

    /**
     * 打印日志
     * 最终通知，在 切入点方法（业务层中增强的方法）之后执行
     */
    @After("allMethodPCRGlobal()")
    public void printLogAfter() {
        System.out.println("Logger 类中的 printLogAfter 方法开始记录日志了...");
    }

    /**
     * 环绕通知
     * 问题：当配置了环绕通知之后，切入点方法没有执行，而通知方法执行了
     * 分析：通过对比动态代理中的环绕通知，发现动态代理的环绕通知有明确的切入点方法调用
     * 解决：Spring 提供了一个接口 ProceedingJoinPint，它有一个 proceed()，此方法相当于明确调用切入点方法
     * 该接口可以作为环绕通知的方法的参数，在程序执行时，Spring 会提供该接口的实现类
     */
    @Around("allMethodPCRGlobal()")
    public Object printLogAround(ProceedingJoinPoint pjp) {
//        System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...");
        Object rtValue = null;

        try {
            Object[] args = pjp.getArgs();  //  得到方法执行所需的参数
            System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...  前置通知");
            rtValue = pjp.proceed(args);    // 切入点方法
            System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...  后置通知");
        } catch (Throwable throwable) {
            System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...  异常通知");
            throwable.printStackTrace();
        } finally {
            System.out.println("Logger 类中的 printLogAround 方法开始记录日志了...  最终通知");
        }
        return rtValue;
    }
}
