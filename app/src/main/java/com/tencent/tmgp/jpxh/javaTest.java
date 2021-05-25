package com.tencent.tmgp.jpxh;

import android.provider.Settings;

import java.util.HashMap;

public class javaTest {

    public static void main(String[] args) {
        long starttime = System.currentTimeMillis();
        solution();
//        solution1();
        long endtime = System.currentTimeMillis();
        long time = endtime - starttime;
        System.out.println("time:" + time + "");
       /* boolean isa = (0x00600000 & 0x00400000) == 0x00400000;
        boolean isa1 = (0x00600000 & 0x00400000) == 0x00400000;
        int ii2 = 0x00400000;
        int b1 = (2 | 1);
        boolean isss = (2 == 2) && (1 == 2);

        Integer i = new Integer(100);
        Integer c1 = new Integer(100);
        boolean i1 = i.equals(c1);
        String a = "hello2";
        final String b = "hello";
        String d = "hello";
        String c = b + 2;
        String e = d + 2;
        System.out.println((a == c));
        System.out.println((a.equals(e)));

        MyClass myClass = new MyClass();
        StringBuffer buffer = new StringBuffer("hello");
        myClass.changeValue(buffer);
        System.out.println(buffer.toString());*/
    }

    public static void solution() {
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
                16, 17, 18, 19, 22, 33, 44, 55, 66, 77, 88, 99, 111, 222, 333, 444, 555, 666, 777, 888, 999
                , 1111, 2222, 3333, 4444, 5555, 6666, 7777, 8888, 9999};
        int sum = 18887;
        for (int i = 0; i < nums.length - 1; i++) {
            for (int k = i + 1; k < nums.length; k++) {
                if (nums[i] + nums[k] == sum) {
                    System.out.println("i and k:" + i + k + "");
                    return;
                }
            }
        }
    }

    public static void solution1() {
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
                16, 17, 18, 19, 22, 33, 44, 55, 66, 77, 88, 99, 111, 222, 333, 444, 555, 666, 777, 888, 999
                , 1111, 2222, 3333, 4444, 5555, 6666, 7777, 8888, 9999};
        int sum = 18887;
        HashMap map = new HashMap();
        for (int i = 0; i < nums.length; i++) {
            int other = sum - nums[i];
            if (map.containsKey(other)) {
                System.out.println("i and k:" + i + map.get(other) + "");
            }
            map.put(nums[i], i);
        }
    }
}

class MyClass {
    void changeValue(StringBuffer buffer) {
        buffer.append("11");
    }
}
