package com.changyue.web;

import java.lang.reflect.Proxy;

/**
 * @program: handwritngmybits
 * @description: 模拟SqlSession
 * @author: 袁阊越
 * @create: 2019-10-22 13:40
 */
public class MySqlSession {

    /**
     * 传入接口
     *
     * @param clazz
     * @return
     */
    public static Object getMapper(Class clazz) {
        return Proxy.newProxyInstance(MySqlSession.class.getClassLoader(), new Class[]{clazz}, new MapperInvocationHandler());
    }

}
