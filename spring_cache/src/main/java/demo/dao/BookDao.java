package demo.dao;

import demo.bean.BookBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class BookDao {
    /**
     * 模拟数据库
     */
    private static Map<Integer, BookBean> bookMap = new HashMap<>();
    @PostConstruct
    private void initDB() {
        BookBean xiyou = new BookBean();
        xiyou.setBookId(1);
        xiyou.setBookName("西游记");
        xiyou.setAuthor("吴承恩");
        xiyou.setPrice(55.5f);
        bookMap.put(1, xiyou);
        BookBean honglou = new BookBean();
        honglou.setBookId(2);
        honglou.setBookName("红楼梦");
        honglou.setAuthor("曹雪芹");
        honglou.setPrice(66.6f);
        bookMap.put(2, honglou);
    }

    public BookBean getBookById(int bookId) {
        return bookMap.get(bookId);
    }

    public BookBean update(BookBean bookBean) {
        bookMap.put(bookBean.getBookId(), bookBean);
        return bookMap.get(bookBean.getBookId());
    }

    public BookBean save(BookBean bookBean) {
        bookMap.put(bookBean.getBookId(), bookBean);
        return bookMap.get(bookBean.getBookId());
    }

    public void delete(int bookId) {
        bookMap.remove(bookId);
    }
}
