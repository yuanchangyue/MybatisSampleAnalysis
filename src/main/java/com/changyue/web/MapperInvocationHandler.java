package com.changyue.web;

import org.apache.ibatis.annotations.Select;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @program: handwritngmybits
 * @description: 处理传递过来的接口
 * @author: 袁阊越
 * @create: 2019-10-22 13:49
 */
public class MapperInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("连接数据库成功");
        Select select = method.getAnnotation(Select.class);
        if (select != null) {
            String s = select.value()[0];
            System.out.println("执行了：" + s);
        }
        if (method.getName().equals("toString")) {
            return proxy.getClass().getInterfaces()[0].getName();
        }
        return null;
    }

}
