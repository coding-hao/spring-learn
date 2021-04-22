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
