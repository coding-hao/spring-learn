package com.demo.dao;

import com.demo.bean.Book;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookDao {

    public List<Book> select();

}
