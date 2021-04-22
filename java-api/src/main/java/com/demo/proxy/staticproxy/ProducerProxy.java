package com.demo.proxy.staticproxy;

/**
 * 定义产家的代理商,也具备卖货的功能
 **/
public class ProducerProxy implements Producer {
    private Producer producer;

    ProducerProxy(Producer producer) {
        this.producer = producer;
    }

    @Override
    public void sell() {
        System.out.println("--------商店卖货前--------");
        producer.sell();
        System.out.println("--------商店卖货后--------");
    }
}

/**
 */
class StaticProxyTest {
    public static void main(String[] args) {
        Producer producer = new Shop();
        ProducerProxy personProxy = new ProducerProxy(producer);
        personProxy.sell();
    }
}
