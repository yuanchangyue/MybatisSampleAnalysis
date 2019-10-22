package com.changyue.test;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @program: handwritngmybits
 * @description:
 * @author: 袁阊越
 * @create: 2019-10-22 15:17
 */
@Component
public class MyFactoryBean implements FactoryBean {

    Class mapperInterface;

    @Override
    public Object getObject() throws Exception {
        return new MapperSession().getMapper(mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public void setMapperInterface(Class mapperInterface) {
        this.mapperInterface = mapperInterface;
    }
}
