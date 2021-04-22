https://developer.aliyun.com/article/713803

https://developer.aliyun.com/article/713828

https://developer.aliyun.com/article/708635

# 模仿mybatis在SpringBoot启动时为接口创建代理实现类

**Mybatis是一个优秀的ORM框架，它支持定制化 SQL、存储过程以及高级映射，对Mybatis不熟悉的可以查看我的这篇文章：[Mybatis原理](https://yq.aliyun.com/articles/713803?spm=a2c4e.11155435.0.0.1d613312OKYETd)，在使用中，我们往往会很惊讶，为啥我只定义了一个接口，就可以进行依赖注入，而且还能对数据库进行操作，这其实是基于`代理模式`来实现的**

**本文将介绍如何实现和Mybatis一样，在SpringBoot启动的时候自动为所有接口创建代理实现类**

### 一、创建核心包

**这个包主要提供注册代理实现类的一些核心类**

#### 1、pom文件如下

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spring-learn</artifactId>
        <groupId>com.example</groupId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>copy-mybatis</artifactId>

    <properties>
        <java.version>1.8</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>


    <build>
        <plugins>
            <!--Compiler-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

#### 2、定义一个基础接口

```java
package com.demo.service;

/**
 * @author CodingTao
 * @Date: 2021-04-22 22:13
 */
public interface BaseService {

    void say();

}

```

#### 3、定义基础接口的实现类

```java
package com.demo.service.impl;

import com.demo.service.BaseService;

/**
 * @author CodingTao
 * @Date: 2021-04-22 22:13
 */
public class DefaultService implements BaseService {
    @Override
    public void say() {
        System.out.println("say");
    }
}

```

#### 4、定义一个代理类

```java
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

```

#### 5、定义代理类实现工厂

```java
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

```

#### 6、定义接口扫描类

```java
package com.demo.proxy;

import com.demo.proxy.ServiceProxyFactoryBean;
import com.demo.service.BaseService;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Set;

/**
 * @author CodingTao
 * @Date: 2021-04-22 22:17
 */
public class ServiceInterfacesScanner extends ClassPathBeanDefinitionScanner {

    public ServiceInterfacesScanner(BeanDefinitionRegistry registry) {
        //false表示不使用ClassPathBeanDefinitionScanner默认的TypeFilter
        super(registry);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        this.addFilter();
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        if (beanDefinitionHolders.isEmpty()) {
            throw new NullPointerException("No interfaces");
        }
        this.createBeanDefinition(beanDefinitionHolders);
        return beanDefinitionHolders;
    }

    /**
     * 只扫描顶级接口
     * @param beanDefinition bean定义
     * @return boolean
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        String[] interfaceNames = metadata.getInterfaceNames();
        return metadata.isInterface() && metadata.isIndependent()&& Arrays.asList(interfaceNames).contains(BaseService.class.getName());
    }

    /**
     * 扫描所有类
     */
    private void addFilter() {
        addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
    }

    /**
     * 为扫描到的接口创建代理对象
     *
     * @param beanDefinitionHolders beanDefinitionHolders
     */
    private void createBeanDefinition(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            GenericBeanDefinition beanDefinition = ((GenericBeanDefinition) beanDefinitionHolder.getBeanDefinition());
            //将bean的真实类型改变为FactoryBean
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanDefinition.getBeanClassName());
            beanDefinition.setBeanClass(ServiceProxyFactoryBean.class);
            beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        }
    }

}

```

#### 7、定义注册类

```java
package com.demo.proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.util.StringUtils;

/**
 * @author CodingTao
 * @Date: 2021-04-22 22:19
 */
public class ProxyRegister implements BeanDefinitionRegistryPostProcessor {
    private String basePackage;

    public ProxyRegister(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (StringUtils.isEmpty(basePackage)) {
            return;
        }
        ServiceInterfacesScanner scanner = new ServiceInterfacesScanner(registry);
        scanner.doScan(basePackage);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}

```

**整个核心包就完成了，接下来定义一个普通项目并使用它**

### 二、创建普通项目

#### 1、引入依赖

```java
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!--引入刚刚定义的核心类-->
<dependency>
  <groupId>com.example</groupId>
  <artifactId>copy-mybatis</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

#### 2、定义一个接口

```java
package com.demo.service;

/**
 * @author CodingTao
 * @Date: 2021-04-22 22:20
 */
public interface UserService extends BaseService{

    String getMethodName();

}

```

#### 3、定义一个controller

```java
package com.demo.controller;

import com.demo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author CodingTao
 * @Date: 2021-04-22 22:21
 */
@RestController
public class TestController {
    @Resource
    private UserService userService;

    @GetMapping("/test")
    public void test() {
        userService.say();
        userService.getMethodName();
    }
}

```

#### 4、配置接口扫描路径

```java
package com.demo.config;

import com.demo.proxy.ProxyRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author CodingTao
 * @Date: 2021-04-22 22:22
 */
@Configuration
public class DemoConfiguration {
    @Bean
    public ProxyRegister proxyRegister() {
        return new ProxyRegister("com.demo.service");
    }
}

```

#### 5、启动

```java
package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author CodingTao
 * @date 2021-04-22 22:08
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

可以看到，我们注入的对象是我们定义的代理类

```java
com.demo.Application

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.7.RELEASE)

2021-04-22 22:35:52.345  INFO 9024 --- [           main] com.demo.Application                     : Starting Application on pc with PID 9024 (D:\work\spring-learn\copy-mybatis\target\classes started by Administrator in D:\work\spring-learn)
2021-04-22 22:35:52.357  INFO 9024 --- [           main] com.demo.Application                     : No active profile set, falling back to default profiles: default
2021-04-22 22:35:53.757  INFO 9024 --- [           main] o.s.c.a.ConfigurationClassPostProcessor  : Cannot enhance @Configuration bean definition 'demoConfiguration' since its singleton instance has been created too early. The typical cause is a non-static @Bean method with a BeanDefinitionRegistryPostProcessor return type: Consider declaring such methods as 'static'.
2021-04-22 22:35:54.289  INFO 9024 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2021-04-22 22:35:54.305  INFO 9024 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2021-04-22 22:35:54.306  INFO 9024 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.34]
2021-04-22 22:35:54.517  INFO 9024 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2021-04-22 22:35:54.517  INFO 9024 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 2036 ms
2021-04-22 22:35:54.745  INFO 9024 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2021-04-22 22:35:54.968  INFO 9024 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-04-22 22:35:54.972  INFO 9024 --- [           main] com.demo.Application                     : Started Application in 3.471 seconds (JVM running for 5.052)
2021-04-22 22:36:00.248  INFO 9024 --- [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2021-04-22 22:36:00.248  INFO 9024 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2021-04-22 22:36:00.255  INFO 9024 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 7 ms
say
执行您的方法：getMethodName
```

