package com.changyue.test;

import com.changyue.config.MyConfig;
import com.changyue.mapper.UserDao;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.Map;

/**
 * @program: handwritngmybits
 * @description: 测试类
 * @author: 袁阊越
 * @create: 2019-10-22 11:29
 */
public class MyTest {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        System.out.println(context.getBean("userDao"));

        /*ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        UserDao userDao = (UserDao) new MapperSession().getMapper(UserDao.class);
        beanFactory.registerSingleton("userDao", userDao);
        //context.getBean(UserService.class).getList();
        */
    }


    public ComboPooledDataSource createDateSource() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass("com.mysql.jdbc.Driver");
            dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8");
            dataSource.setUser("root");
            dataSource.setPassword("root");
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    @Test
    public void testMybatis() {
        DataSource dataSource = createDateSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment =
                new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(UserDao.class);
        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(configuration);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDao mapper = sqlSession.getMapper(UserDao.class);
        List<Map<String, Object>> list = mapper.getList();
        System.out.println(list);
    }


    @Test
    public void testMapperSession() {
        MapperSession mapperSession = new MapperSession();
        UserDao userDao = (UserDao) MapperSession.getMapper(UserDao.class);
        userDao.getList();
    }

}


























