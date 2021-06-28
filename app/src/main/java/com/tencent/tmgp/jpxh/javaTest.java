package com.tencent.tmgp.jpxh;

import android.provider.Settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class javaTest {

    private int i;

    public static void main(String[] args) {
       /* javaTest test = new MyClass();
        test.test1();*/
       /* String string = "apple water  rice balana apple  water rice rice rice";
        countString(string);*/
       /* int i, j;
        char c = '*';
        for (i = 0; i < 5; i++) {
            for (j = 0; j <= i; j++)
                System.out.print("* ");
            System.out.println();
        }*/

//        int i=123; long j=456;
//        j = i;
//        j=(long)i;
//        i=(int)j;
     /*   int x = 1;
        int y = 2;
        int z = 3;
        y += z--/++x;
        System.out.println(y);*/
        /*float z = 1.234f ;
        double w = 1.23;
        System.out.println(z>w);
        String a = "Programming";
        String b = new String("Programming");
        String c = "Program" + "ming";
        System.out.println(a == b);
        System.out.println(a == c);
        System.out.println(a.equals(b));
        System.out.println(a.equals(c));*/

       /* Integer i=new Integer(0);
        add2(i);
        System.out.println(i.intValue());
*/
       /* String s11 = "hello";
        String s22 = "hello";
        System.out.println(s11 == s22);

        long starttime = System.currentTimeMillis();
        solution();
        String s = "11";
        String s1 = "\'a\'";
        System.out.println(s1.length());
        //        solution1();
        long endtime = System.currentTimeMillis();
        long time = endtime - starttime;
        System.out.println("time:" + time + "");*/
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

    public static void add2(Integer i) {
        int val = i.intValue();
        val += 3;
        i = new Integer(val);
    }

    public static void countString(String str) {
        String[] s = str.split(" ");
        Map<String, Integer> map = new HashMap<>();
        int num = 0;
        for (int i = 0; i < s.length; i++) {
            if (!s[i].isEmpty()) {
                if (map.containsKey(s[i])) {
                    int value = map.get(s[i]);
                    map.put(s[i], ++value);
                } else {
                    map.put(s[i], 1);
                }
                num++;
            }
        }
        Set<Map.Entry<String, Integer>> entries = map.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            System.out.println(entry.getKey() + "出现" + entry.getValue() + "次");
        }
        System.out.println("共有单词数" + num);
    }

    void test1() {
        test2();
    }

    void test2() {
        System.out.println("father2");
    }

}

class MyClass extends javaTest {
    void test2() {
        System.out.println("son2");
    }

    public static void main(String[] args) {
        javaTest test = new MyClass();
        test.test1();
    }
}
