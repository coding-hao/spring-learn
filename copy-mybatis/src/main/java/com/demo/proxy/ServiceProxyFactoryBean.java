package com.demo.proxy;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author CodingTao
 * @Date: 2021-04-22 22:15
 */
public class ServiceProxyFactoryBean <T> implements FactoryBean<T> {
    private Class<T> interfaces;

    public ServiceProxyFactoryBean(Class<T> interfaces) {
        this.interfaces = interfaces;
    }

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(interfaces.getClassLoader(), new Class[]{interfaces},
                new ServiceProxy<>(interfaces));
    }

    @Override
    public Class<?> getObjectType() {
        return interfaces;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
