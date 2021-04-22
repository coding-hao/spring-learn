package com.demo;

import org.omg.CORBA.portable.InvokeHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

interface Test{
    public void say();
}

interface invokeHandler{
    Object invoke(Object object, Method method, Object ... agrs);
}

public class ProxyDemo {

    public static void main(String[] args) {
       /* Test test=new Test() {
            @Override
            public void say() {
                System.out.println("say:hello");
            }
        };
        test.say();*/


    }

    public Test newProxyInstance(InvokeHandler handler, Class<?> clazz) {
        return new Test() {
            @Override
            public void say() {
                try {
                    Method sayMethod = clazz.getMethod("say");
                    sayMethod.invoke(this, sayMethod);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


}
