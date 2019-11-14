package com.changyue.test;

import com.changyue.config.MyConfig;
import com.changyue.mapper.UserDao;
import com.changyue.service.UserService;
import com.changyue.web.MySqlSession;
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
        UserService userService = (UserService) context.getBean(UserService.class);
        userService.getList();

        /*ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        UserDao userDao = (UserDao) new MapperSession().getMapper(UserDao.class);
        beanFactory.registerSingleton("userDao", userDao);
        //context.getBean(UserService.class).getList();
        */

  /*    UserDao userDao = (UserDao) MySqlSession.getMapper(UserDao.class);
        userDao.getList();
*/
    }


    /**
     * 使用c3p0创建数据源
     *
     * @return dataSource
     */
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


    /**
     * 测试官方提供非xml的MyBatis配置实现
     */
    @Test
    public void testMybatis() {
        DataSource dataSource = createDateSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();

        //mybatis-config.xml 中的配置相似 构建环境 添加mapper文件
        Environment environment =
                new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(UserDao.class);

        //根据以上的配置 创建出一个sqlSessionFactory
        SqlSessionFactory sqlSessionFactory =
                new SqlSessionFactoryBuilder().build(configuration);

        //获得sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDao mapper = sqlSession.getMapper(UserDao.class);

        //执行mapper
        List<Map<String, Object>> list = mapper.getList();
        System.out.println(list);
    }


    /**
     * 测试自己的SqlSession
     */
    @Test
    private void testMapperSession() {

        UserDao userDao = (UserDao) MySqlSession.getMapper(UserDao.class);
        userDao.getList();

    }

}


























