package com.tencent.tmgp.jpxh;

class javaTest {

    public static void main(String[] args) {
        boolean isa = (0x00600000 & 0x00400000) == 0x00400000;
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
        System.out.println(buffer.toString());
    }
}

class MyClass {

    void changeValue(StringBuffer buffer) {
        buffer.append("11");
    }
}
