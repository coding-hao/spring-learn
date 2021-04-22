package com.demo.controller;

import com.demo.service.IPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("controller")
@RestController
public class Controller {
    @Autowired
    List<IPerson> persons;
    @Autowired
    Map<String, IPerson> personMaps;


    @GetMapping("getList")
    @ResponseBody
    public String getList() {
        return persons.toString();
    }

    @GetMapping("getMap")
    @ResponseBody
    public String getMap() {
        return personMaps.toString();
    }


}
