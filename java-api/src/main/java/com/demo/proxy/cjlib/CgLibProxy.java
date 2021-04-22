package com.demo.proxy.cjlib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 **/
public class CgLibProxy implements MethodInterceptor {
    private Shop3 shop3;

    CgLibProxy(Shop3 shop3) {
        this.shop3 = shop3;
    }

    Shop3 proxy() {
        // 创建Enhancer对象
        Enhancer enhancer = new Enhancer();
        // 设置代理的目标类
        enhancer.setSuperclass(Shop3.class);
        // 设置回调方法, this代表是当前类, 因为当前类实现了CallBack
        enhancer.setCallback(this);
        return (Shop3) enhancer.create();
    }

    //这个方法就是回调方法了
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("----------商店卖货前----------");
        Object invoke = method.invoke(shop3, objects);
        System.out.println("----------商店卖货后----------");
        return invoke;
    }
}

/**
 */
class TestCglibProxy{
    public static void main(String[] args) {
        Shop3 shop3 = new Shop3();
        Shop3 proxy = new CgLibProxy(shop3).proxy();
        proxy.sell();
    }
}
