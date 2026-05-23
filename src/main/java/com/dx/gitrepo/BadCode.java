package com.dx.gitrepo;

public class BadCode {
    
    public String method1(String a, String b, String c, String d, String e, String f, String g, String h) {
        String result = "";
        result = result + a;
        result = result + b;
        result = result + c;
        return result;
    }

    public void method2() {
        try {
            int x = 1/0;
        } catch(Exception e) {
        }
    }
}
