package com.itheima.test07;

/**
 * 多线程调试
 */
public class MainClass {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new MyRunnable(), "曹操");
        Thread thread2 = new Thread(new MyRunnable(), "刘备");
        Thread thread3 = new Thread(new MyRunnable(), "孙权");
        thread1.start();
        thread2.start();
        thread3.start();
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "____进入");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "____离开");
    }
}
