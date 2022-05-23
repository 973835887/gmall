package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootTest
public class AppThreadPoolTest {

    @Qualifier("corePool")
    @Autowired
    ThreadPoolExecutor corePool;

    @Test
    public void zuheTest(){
        CompletableFuture<Void> a = CompletableFuture.runAsync(() -> {
            System.out.println("打印A");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(" A ");
        }, corePool);

        CompletableFuture<Void> b = CompletableFuture.runAsync(() -> {
            System.out.println("输出B");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(" B ");
        }, corePool);

        CompletableFuture<Void> c = CompletableFuture.runAsync(() -> {
            System.out.println("打印C");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(" C ");
        }, corePool);

        CompletableFuture.allOf(a, b, c)
                .whenComplete((x,y)->{
                    System.out.println("a = " + x);
                    System.out.println("b = " + y);
                    System.out.println(" D ");
                });

    }

    @Test
    public void thenTest() throws ExecutionException, InterruptedException {
        //then : 启动一个无返回值的线程任务
        //then : 传入consumer  接参数但是无返回值
        //then :传入参数有返回值
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程 = " + Thread.currentThread().getName());
            int i = 1 + 1;
            return i;
        }, corePool).thenApply((result) -> {
            System.out.println("当前线程 = " + Thread.currentThread().getName());
            result = result + 10;
            return result;
        }).thenApply((result) -> {
            System.out.println("当前线程 = " + Thread.currentThread().getName());
            return result + "A";
        });

        String s = future.get();
        System.out.println("s = " + s);
    }

    @Test
    public void lianshiTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "corePool");
            Double ds = Math.random() * 100;
            int i = ds.intValue() / 0;
            return i;
        }, corePool)
                .exceptionally((ex)->{
                    System.out.println("异常原因 = " + ex);
                    return 299;
                });

        Integer integer = future.get();
        System.out.println("integer = " + integer);
    }

    @Test
    public void exceptionTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName()+"corePool");
            Double ds = Math.random() * 100;
            int i = ds.intValue()/0;
            return i;
        },corePool);

        CompletableFuture<Integer> exceptionally = future.exceptionally((t) -> {
            System.out.println("上次的异常 = " + t);
            return 3;
        });


        Integer integer = exceptionally.get();
        System.out.println("integer = " + integer);

    }

    @Test
    public void bianpaiTest(){
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "正在计算!");
                int i = 10/0;
        }, corePool);

//  同一个线程      future.whenComplete((t,u)->{
//            if(u != null){
//                //完成之后的异常 产生异常
//                System.out.println("u = " + u);
//            }else
//            //完成之后的返回结果
//            //正常走这个 t为null
//            System.out.println("21:30");
//            System.out.println("t = " + t);
//        });
        //开同一个线程池的新线程做事,不加就是开新的线程池
//        future.whenCompleteAsync((a,b)->{
//            System.out.println("线程 : " + Thread.currentThread().getName());
//        },corePool);

        //异常处理  有异常才会启用
        future.exceptionally((t)->{
            System.out.println("上次的异常= " + t);
            return null;
        });

    }

    @Test
    public void startAsync(){
        CompletableFuture<Double> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName()+"corePool");
            return Math.random() * 100;
        },corePool);

        //join  任务执行完成之后再执行
//        Double join = future.join();

        //阻塞等待
        Double aDouble = null;
        try {
            aDouble = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("join = " + aDouble);
    }
}
