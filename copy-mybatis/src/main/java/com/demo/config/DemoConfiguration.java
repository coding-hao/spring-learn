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
