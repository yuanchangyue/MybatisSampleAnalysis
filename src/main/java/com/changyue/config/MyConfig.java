package com.changyue.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @program: handwritngmybits
 * @description: 配置类
 * @author: 袁阊越
 * @create: 2019-10-22 11:14
 */
@ComponentScan("com.changyue")
@Configuration
@ImportResource("classpath:ApplicationContext.xml")
@MyScan
public class MyConfig {

/*    @Bean
    public UserDao userDao() {
        return (UserDao) MySqlSession.getMapper(UserDao.class);
    }*/

/*
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(createDateSource());
        return factoryBean.getObject();
    }

    @Bean
    public MapperFactoryBean<UserDao> userMapper() throws Exception {
        MapperFactoryBean<UserDao> factoryBean = new MapperFactoryBean<>(UserDao.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory());
        return factoryBean;
    }*/

    /*   *//**
     * 配置dataSource
     *
     * @return ComboPooledDataSource
     *//*
    @Bean
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
    }*/

}
