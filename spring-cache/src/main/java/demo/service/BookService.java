package demo.service;


import demo.bean.BookBean;

public interface BookService {

    public BookBean getBookById(int bookId);

    public BookBean updata(BookBean bookBean);

    public BookBean save(BookBean bookBean);

    public String delete(int bookId);

}
