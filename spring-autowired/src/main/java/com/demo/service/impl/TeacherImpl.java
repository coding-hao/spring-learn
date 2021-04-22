package com.demo.service.impl;

import com.demo.service.IPerson;
import org.springframework.stereotype.Component;

@Component("teacher")
public class TeacherImpl implements IPerson {

    @Override
    public void doWork() {
        System.out.println("I am teaching");
    }

}
