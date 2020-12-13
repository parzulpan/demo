package cn.parzulpan.test;

import java.sql.Blob;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author : parzulpan
 * @Time : 2020-12-13
 * @Desc :
 */

public class ThreadLocalTest {
//    public static Map<String, Object> data = new ConcurrentHashMap<>();   // 使用 ConcurrentHashMap，是线程安全的
    public static ThreadLocal<Object> data = new ThreadLocal<>();   // 使用 ThreadLocal，同样是线程安全的

    private static Random random = new Random();

    public static class Task implements Runnable {
        @Override
        public void run() {
            // 在 Run 方法中，随机生成一个变量（线程要关联的数据），然后以当前线程名为 key 保存到 map 中
            Integer i = random.nextInt(1000);

            // 获取当前线程名
            String name = Thread.currentThread().getName();
            System.out.println("线程["+name+"]生成的随机数是：" + i);

//            data.put(name, i);
            data.set(i);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 做一些中间操作
            new OrderService().createOrder();

            // 在 Run 方法结束之前，以当前线程名获取出数据并打印。查看是否可以取出操作
//            Object o = data.get(name);
            Object o = data.get();
            System.out.println("在线程["+name+"]快结束时取出关联的数据是：" + o);

        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new Thread(new Task()).start();
        }
    }
}


class OrderService {

    public void createOrder(){
        String name = Thread.currentThread().getName();
//        System.out.println("OrderService 当前线程[" + name + "]中保存的数据是：" +
//                ThreadLocalTest.data.get(name));
        System.out.println("OrderService 当前线程[" + name + "]中保存的数据是：" +
                ThreadLocalTest.data.get());
        new OrderDao().saveOrder();
    }
}

class OrderDao {
    public void saveOrder(){
        String name = Thread.currentThread().getName();
//        System.out.println("OrderDao 当前线程[" + name + "]中保存的数据是：" +
//                ThreadLocalTest.data.get(name));
        System.out.println("OrderDao 当前线程[" + name + "]中保存的数据是：" +
                ThreadLocalTest.data.get());
    }
}
