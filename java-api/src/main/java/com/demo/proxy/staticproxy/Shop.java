package com.demo.proxy.staticproxy;

/**
 * 定义一个商店,帮产家卖货
 **/
public class Shop implements Producer {
    @Override
    public void sell() {
        System.out.println("商店进行卖货");
    }
}
