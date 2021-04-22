package com.demo;

import lombok.val;
import org.apache.ibatis.annotations.Select;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

interface UserMapper{
    @Select("select name from user where id=#{id} and name=#{name}")
    List<String> getUser(int id, String name);
}

public class JavaApplication {

    public static void main(String[] args) {
        UserMapper userMapper = (UserMapper) Proxy.newProxyInstance(JavaApplication.class.getClassLoader(),
                new Class[]{UserMapper.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("method_name:" + method.getName());
                        Select select = method.getAnnotation(Select.class);
                        Map<String, Object> argNameMap = buildMethodArgNameMap(method, args);

                        if (select != null) {
                            String[] value = select.value();
                            System.out.println(Arrays.toString(value));
                            String sql = value[0];
                            System.out.println("解析后的sql:"+parseSQL(sql, argNameMap));
                            System.out.println("返回值:"+method.getReturnType());
                            System.out.println("返回泛型是什么:"+method.getGenericReturnType());

                        }
                        return Arrays.asList("小明");
                    }
                });
        List<String> list = userMapper.getUser(1,"小明");
        System.out.println(list.toString());

    }

    public static String parseSQL(String sql ,Map<String,Object> argNameMap) {
        String parseSQL = "";
        StringBuilder stringBuilder = new StringBuilder();
        int length = sql.length();
        for (int i = 0; i < length; i++) {
            char c = sql.charAt(i);
            if (c == '#') {
                int nextIndex = i + 1;
                char nextChar = sql.charAt(nextIndex);
                if (nextChar != '{') {
                    throw new RuntimeException(String.format("这里应该为#{\nsql:%s\nindex:%D", stringBuilder.toString(), nextIndex));
                }
                StringBuilder argSB = new StringBuilder();
                i = parseArg(argSB, sql, nextIndex);
                String argName = argSB.toString();
                Object argValue = argNameMap.get(argName);
                if (argValue == null) {
                    throw new RuntimeException(String.format("找不到参数值:%s", argName));
                }
                stringBuilder.append(argValue.toString());
                continue;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static int parseArg(StringBuilder argSB,String sql,int nextIndex) {
        nextIndex++;
        for (; nextIndex < sql.length(); nextIndex++) {
            char c = sql.charAt(nextIndex);
            if (c != '}') {
                argSB.append(c);
                continue;
            }
            if (c == '}') {
                return nextIndex;
            }
        }
        throw new RuntimeException(String.format("缺少右括号\nindex:%d", nextIndex));
    }


    public static Map<String, Object> buildMethodArgNameMap(Method method, Object[] args) {
        Map<String, Object> maps = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        int index[]={0};
        Arrays.asList(parameters).forEach(parameter -> {
            String name = parameter.getName();
            maps.put(name, args[index[0]]);
            index[0]++;
            System.out.println("name:"+name);

        });
        return maps;
    }

}
