package com.itheima.test03;

/**
 * 步出调试的使用
 */
public class MainClass {
    public static void main(String[] args) {
        int num = method();
        num = num * 2;
        num = num % 3;
        System.out.println(num);
    }

    private static int method() {
        System.out.println("这里的大山不一般，");
        System.out.println("有花有草我喜欢，");
        System.out.println("有花有草我喜欢啊，");
        System.out.println("这里的大山不一般！！");
        int a = 10;
        int b = 10;
        int c = 10;
        int d = 10;
        int e = 10;

        return a + b + c + d + e;
    }

}
