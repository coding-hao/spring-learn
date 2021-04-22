package com.demo.proxy.dynamic;

import java.lang.reflect.Proxy;

public class Producer2Proxy {
    public static void main(String[] args) {
        Producer2 producer2 = new Shop2();
        Producer2 producerProxy = (Producer2) Proxy.newProxyInstance(producer2.getClass().getClassLoader(),
                producer2.getClass().getInterfaces(), (proxy, method, args1) -> {
                    System.out.println("----------商店卖货前--------");
                    Object invoke = method.invoke(producer2,args1);
                    System.out.println("----------商店卖货后--------");
                    return invoke;
                });
        producerProxy.sell();
    }
}
