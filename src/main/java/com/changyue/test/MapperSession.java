package com.changyue.test;

import java.lang.reflect.Proxy;

/**
 * @program: handwritngmybits
 * @description: 模拟SqlSession
 * @author: 袁阊越
 * @create: 2019-10-22 13:40
 */
public class MapperSession {


    public static Object getMapper(Class clazz) {
        return Proxy.newProxyInstance(MapperSession.class.getClassLoader(), new Class[]{clazz}, new MapperInvocationHandler());
    }

}
