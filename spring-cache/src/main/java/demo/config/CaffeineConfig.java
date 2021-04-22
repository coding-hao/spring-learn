package demo.config;

import com.github.benmanes.caffeine.cache.*;
import demo.bean.BookBean;
import demo.dao.BookDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    @Autowired
    BookDao bookDao;

    @Autowired
    CacheLoader cacheLoader;

    @Bean
    @Primary
    public CacheManager caffeineCache() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder()
                //cache的初始容量值
                .initialCapacity(10)
                //maximumSize用来控制cache的最大缓存数量，maximumSize和maximumWeight不可以同时使用，
                .maximumSize(100)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .removalListener((Integer key, Object value, RemovalCause cause) ->
                        System.out.printf("时间:%d,Key:%d,value:%s,移除原因(%s)%n",System.currentTimeMillis()/1000, key,value.toString(),cause)
                )
                //使用refreshAfterWrite必须要设置cacheLoader
                .refreshAfterWrite(5, TimeUnit.SECONDS);
        cacheManager.setCaffeine(caffeine);
        //缓存加载策略，当key不存在或者key过期之类的都可以通过CacheLoader来重新获得数据
        cacheManager.setCacheLoader(cacheLoader);
        return cacheManager;
    }

    @Bean
    public CacheLoader<Object, Object> cacheLoader() {
        CacheLoader<Object, Object> cacheLoader = new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) throws Exception {
                System.out.println("重新从数据库加载数据:" + key);
                return bookDao.getBookById((int)key);
            }

            // 达refreshAfterWrite所指定的时候会触发这个事件方法
            @Override
            public Object reload(Object key, Object oldValue) throws Exception {
                //可以在这里处理重新加载策略，本例子，没有处理重新加载，只是返回旧值。
                return oldValue;
            }
        };
        return cacheLoader;
    }

}
