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
