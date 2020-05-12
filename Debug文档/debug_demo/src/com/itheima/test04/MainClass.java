package com.itheima.test04;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 调试案例，并发修改异常
 */
public class MainClass {
    public static void main(String[] args) {
        System.out.println("begin...");
        operatorList();
        System.out.println("end...");
    }

    private static void operatorList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("贾玲");
        list.add("贾冰");
        list.add("贾岛");
        list.add("贾宝玉");
        //获取集合迭代器
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            if(name.equals("贾冰")) {
                //改变集合元素个数
                list.add("假货");
            }
        }
    }
}
