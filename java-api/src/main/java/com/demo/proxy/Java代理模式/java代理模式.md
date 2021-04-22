# 代理模式

## 一、 定义

​        为其他对象提供一种[代理](https://baike.baidu.com/item/代理)以控制对这个对象的访问。在某些情况下，一个对象不适合或者不能直接引用另一个对象，而代理对象可以在客户端和目标对象之间起到中介的作用。

​        例子：电脑桌面的快捷方式。电脑对某个程序提供一个快捷方式（代理对象），快捷方式连接客户端和程序，客户端通过操作快捷方式就可以操作那个程序。

![proxy](E:\新建文件夹\总结\Java动态代理\picture\ba812a6876b72f9c296e6d965bcbec2c1e810820.png)

## 二、 代理模式中的角色

- **抽象角色**：为真实对象和代理对象提供一个共同的接口，一般是抽象类或者接口。
- **代理角色**：代理角色内部含有对真实对象的引用，从而可以操作真实对象，同时代理对象提供与真实对象相同的接口以便在任何时刻都能够代替真实对象。同时，代理对象可以在执行真实对象的操作时，附加其他操作，相当于对真实对象的功能进行拓展。
- **真实角色**：最终引用的对象。



## 三、 静态代理

**代理类在程序运行前就已经存在,那么这种代理方式被成为`静态代理`**

#### 1. 定义抽象角色

```java
/**
 * 定义一个产家,提供卖货的功能
 **/
public interface Producer {
    void sell();
}
```



#### 2. 定义一个真实角色

```
/**
 * 定义一个商店,帮产家卖货
 **/
public class Shop implements Producer {
    @Override
    public void sell() {
        System.out.println("商店进行卖货");
    }
}
```

#### 3. 定义一个代理类和测试类

```java
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
```



## 四、 动态代理

​        **代理类在程序运行时创建的代理方式被称为 动态代理，如果目标对象实现了接口,采用JDK的动态代理，如果目标对象没有实现接口，必须采用cglib动态代理**。



#### 1. JDK动态代理

##### （1） 定义一个厂商

```java
/**
 * 定义一个厂商
 **/
public interface Producer2 {
    void sell();
}
```



##### （2）定义一个真实角色

```java
/**
 * 定义商家
 **/
public class Shop2 implements Producer2 {
    @Override
    public void sell() {
        System.out.println("商店进行卖货");
    }
}
```



##### （3）实现代理

```java

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
```





## 五、CGLib代理

目标类`不能为final`,目标对象的方法如果为`final / static`，那么就不会被拦截，即不会执行目标对象额外的业务方法

#### （1）引入依赖

Spring环境下不需要，因为Spring-Core里已经引入了

```
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <version>3.2.12</version>
</dependency>
```

#### （2）创建一个目标类

```java
/**
 * 真正实现类
 **/
public class Shop3 {
    public void sell() {
        System.out.println("商店进行卖货");
    }
}
```

#### （3）创建CGLib的工厂类和测试类

```java
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
```





## 六、三种代理方式的优缺点

#### 1、静态代理：

可以做到在不修改目标对象的功能前提下,对目标功能扩展

**缺点**：

> 代理对象需要与目标对象实现一样的接口,所以会有很多代理类,类太多.同时,一旦接口增加方法,目标对象与代理对象都要维护

#### 2、JDK动态代理

代理对象不需要实现接口, 利用JDK的API,动态的在内存中构建代理对象(需要我们指定创建代理对象/目标对象实现的接口的类型)

**缺点**：

> 目标对象一定要实现接口,否则不能用动态代理

#### 3、CGLib代理

> 静态代理和动态代理模式都是要求目标对象是实现一个接口的目标对象,但是有时候目标对象只是一个单独的对象,并没有实现任何的接口,这个时候就可以使用以目标对象类实现代理

## 七、什么时候使用代理模式

1、当我们想要隐藏某个类时，可以为其提供代理。

2、当一个类需要对不同的调用者提供不同的调用权限时，可以使用代理类来实现（代理类不一定只有一个，我们可以建立多个代理类来实现，也可以在一个代理类中进行权限判断来进行不同权限的功能调用）。

3、当我们要扩展某个类的某个功能时，可以使用代理模式，在代理类中进行简单扩展（只针对简单扩展，可在引用委托类的语句之前与之后进行）。

## 八、JDK代理和CGLib代理的区别

> ```
> JDK动态代理`使用Java的反射技术生成代理类，只能代理实现了接口的类，没有实现接口的类不能实现动态代理，`CGLib`会在运行时动态的生成一个被代理类的子类，子类重写了被代理类中所有`非final`的方法，在子类中采用方法拦截的技术拦截所有父类方法的调用，不需要被代理类对象实现接口，从而`CGLIB`动态代理效率比Jdk动态代理反射技术`效率要高
> ```