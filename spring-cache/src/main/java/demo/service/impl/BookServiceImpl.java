package demo.service.impl;

import demo.bean.BookBean;
import demo.dao.BookDao;
import demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
//可以在此处统一指定cacheNames的名称
@CacheConfig(cacheNames = {"book"})
public class BookServiceImpl implements BookService {

    @Autowired
    BookDao bookDao;

    /**
     * 如果缓存存在，直接读取缓存值；如果缓存不存在，则调用目标方法，并将结果放入缓存
     * value、cacheNames：两个等同的参数（cacheNames为Spring 4新增，作为value的别名），用于指定缓存存储的集合名
     * key：缓存对象存储在Map集合中的key值，非必需，默认按照函数的所有参数组合作为key值，若自己配置需使用SpEL表达式，比如：@Cacheable(key = “#p0”)：使用函数第一个参数作为缓存的key值
     *
     * @param bookId
     * @return
     */
    @Cacheable(cacheNames = {"book"}, key = "#bookId")
//    @Cacheable(value = "book" ,key = "targetClass + methodName +#p0")
    @Override
    public BookBean getBookById(int bookId) {
        System.out.println("查询数据库");
        return bookDao.getBookById(bookId);
    }

    @CachePut(cacheNames = {"book"}, key = "#bookBean.bookId")
    @Override
    public BookBean updata(BookBean bookBean) {
        System.out.println("更新数据库:" + bookBean.toString());
        return bookDao.update(bookBean);
    }

    @CachePut(cacheNames = {"book"}, key = "#bookBean.bookId")//写入缓存，key为user.id,一般该注解标注在新增方法上
    @Override
    public BookBean save(BookBean bookBean) {
        System.out.println("保存至数据库:" + bookBean.toString());
        return bookDao.save(bookBean);
    }

    /**
     * CacheEvict 用来从缓存中移除相应数据
     *  allEntries=true:方法调用后清空所有cacheName为book的缓存
     *  beforeInvocation=true:方法调用前清空所有缓存
     *
     * @param bookId
     * @return
     */

    @CacheEvict(cacheNames = {"book"})
    @Override
    public String delete(int bookId) {
        bookDao.delete(bookId);
        return "成功";
    }


}
