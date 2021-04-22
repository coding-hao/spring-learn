package demo.controller;

import demo.bean.BookBean;
import demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("book")
public class BookController {

    @Autowired
    BookService bookService;

    @GetMapping("get/{bookId}")
    @ResponseBody
    public BookBean getBook(@PathVariable("bookId") int bookId) {
        System.out.println("时间:"+System.currentTimeMillis()/1000+",查询book接口被调用");
        return bookService.getBookById(bookId);
    }

    @PostMapping("updata")
    @ResponseBody
    public BookBean updata(@RequestBody BookBean bookBean) {
        System.out.println("时间:"+System.currentTimeMillis()/1000+",更新book接口被调用");
        return bookService.updata(bookBean);
    }
    @PostMapping("save")
    @ResponseBody
    public BookBean save(@RequestBody BookBean bookBean) {
        System.out.println("时间:"+System.currentTimeMillis()/1000+",保存book接口被调用");
        return bookService.save(bookBean);
    }

    @PostMapping("delete/{bookId}")
    @ResponseBody
    public String delete(@PathVariable("bookId") int bookId) {
        System.out.println("时间:"+System.currentTimeMillis()/1000+",删除book接口被调用");
        return bookService.delete(bookId);
    }
}
