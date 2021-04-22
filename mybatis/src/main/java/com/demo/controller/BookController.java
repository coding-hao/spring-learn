package com.demo.controller;

import com.demo.bean.Book;
import com.demo.dao.BookDao;
import com.demo.dao.BookDao2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("book")
@RestController
public class BookController {
    @Autowired
    BookDao bookDao;
    @Autowired
    BookDao2 bookDao2;

    @GetMapping("select")
    @ResponseBody
    public List<Book> select() {
        bookDao2.select2();
       return bookDao.select();
    }


}
