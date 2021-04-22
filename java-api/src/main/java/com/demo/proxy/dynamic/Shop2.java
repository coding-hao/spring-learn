package com.demo.proxy.dynamic;

/**
 * 定义商家
 **/
public class Shop2 implements Producer2 {
    @Override
    public void sell() {
        System.out.println("商店进行卖货");
    }
}