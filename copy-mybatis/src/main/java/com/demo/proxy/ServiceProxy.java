package com.demo.proxy;

import com.demo.service.impl.DefaultService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author CodingTao
 * @Date: 2021-04-22 22:14
 */
public class ServiceProxy <T> implements InvocationHandler {
    private Class<T> interfaces;
    ServiceProxy(Class<T> interfaces) {
        this.interfaces = interfaces;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(interfaces)) {
            System.out.println("执行您的方法：" + method.getName());
            return method.getName();
        } else {
            return method.invoke(new DefaultService(), args);
        }
    }


}
