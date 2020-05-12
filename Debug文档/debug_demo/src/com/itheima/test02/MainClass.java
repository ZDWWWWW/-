package com.itheima.test02;

/**
 * 步过和步入调试的使用
 */
public class MainClass {
    public static void main(String[] args) {
        int a1 = add(1, 2);
        System.out.println(a1);
        int a2 = add(2, 3);
        System.out.println(a1 + a2);
        int result = a1 + a2;
        System.out.println(result);
    }

    private static int add(int a, int b) {
        int result = a + b;
        return result;
    }
}
