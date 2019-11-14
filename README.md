# Mybatis原理浅析和践行
> 官方网站的提供非xml的自定的的类 实现的mybatis创建SqlSessionFactory和进行配置
>
> https://mybatis.org/mybatis-3/getting-started.html
```java
//获得数据源
DataSource dataSource = BlogDataSourceFactory.getBlogDataSource();
//初始化事务,Mybatis的运行使用到事务
TransactionFactory transactionFactory = new JdbcTransactionFactory();
//初始化Mybatis的运行环境
Environment environment = new Environment("development", transactionFactory, dataSource);
//在以上初始化的环境中，获得配置信息
Configuration configuration = new Configuration(environment);
//添加mapper类到配置类中
configuration.addMapper(BlogMapper.class);
//创建出sqlSessionFatory
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration); 
```

**使用以上官方的代码**

* 配置数据源
```java
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
```
* 创建SqlSession后,获得UserDao接口,直接就可以使用接口方法进行数据库的操作
```java
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
```

![](https://github.com/yuanchangyue/MybatisSampleAnalysis/blob/master/img/%E6%89%A7%E8%A1%8C%E6%88%90%E5%8A%9F2.png?raw=true)

> 通过以上的Mybatis的代码可以知道, UserDao接口我们是没有自己去实现的,
> 接口也是不能直接new出来的,只能去实现接口中的方法 
* 实现接口1 匿名内部类
```java
 UserDao userDao = new UserDao() {
            @Override
            public List<Map<String, Object>> getList() {
                return null;
            }
        };
```
* 实现接口2 实现类
```java
public class UserDaoImpl implements UserDao {

    @Override
    public List<Map<String, Object>> getList() {

        //TODO 代码逻辑
        
        return null;
    }
}
```
> **接口是不能实例化的，MyBatis是怎么调用接口中的方法的呢？**
>
> **为什么在接口上使用@Select等注解写上sql 就可以执行数据库操作？**

选中`getMapper`使用idea的快捷键`ctrl`+`alt`+`b`  -> 查看`getMapper`实现类

![](https://github.com/yuanchangyue/MybatisSampleAnalysis/blob/master/img/getMapper.png?raw=true)

最后找到：

![](https://github.com/yuanchangyue/MybatisSampleAnalysis/blob/master/img/newProxyInstance.png?raw=true)

**可以知道Mybatis使用的是jdk的是动态代理,传入一个接口返回实现这个接口的对象**

- 创建一个类似SqlSession , 使用动态代理

  ```java
  /**
   * @program: handwritngmybits
   * @description: 模拟SqlSession
   * @author: 袁阊越
   * @create: 2019-10-22 13:40
   */
  public class MapperSession {
  
      static Object getMapper(Class clazz) {
          return Proxy.newProxyInstance(MapperSession.class.getClassLoader(), new Class[]{clazz}, new MapperInvocationHandler());
      }
  
  }
  ```

- 动态代理的InvocationHandler

```java

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
        return null;
    }
}
```

- UserDao

```java
/**
 * @program: handwritngmybits
 * @description: 测试用户的dao接口
 * @author: 袁阊越
 * @create: 2019-10-22 10:57
 */
public interface UserDao {

    /**
     * 测试获得list
     *
     * @return
     */
    @Select("select * from tb_user ")
    List<Map<String, Object>> getList();

}

```

- 测试类

```java
    @Test
    public void testMapperSession() {
        UserDao userDao = (UserDao) MapperSession.getMapper(UserDao.class);
        userDao.getList();
    }
```

结果：

![](https://github.com/yuanchangyue/MybatisSampleAnalysis/blob/master/img/%E6%89%A7%E8%A1%8C%E6%88%90%E5%8A%9F.png?raw=true)

**使用动态代理，在InvocationHandler中获取接口方法上的Annotation可以获取注解上面的sql语句，执行数据库的操作**

-----



## Mybatis 是如何和Spring连接起来的？

> 上面MyBatis使用使用动态代理创建了实现的接口的对象，那么Spring是怎么把产生的代理对象注入到容器中？
> 同样如何将自己产生的对象或者是三方产生的对象交给Spring管理？
> 平时使用的@Service和@Component等注解，是将类交给Spring管理，由Spring去实例化对象。

+ `@Bean`  可行 但是代码重复

```java
   @Bean
    public UserDao userDao() {
        return (UserDao) MapperSession.getMapper(UserDao.class);
    }
```

+ `registerSingleton`  可行 ， 在一些场景下面是不能拿到 `AnnotationConfigApplicationContext` (上下文) 比如：在web开发环境下面，在xml中初始化了上下文。

```java
 AnnotationConfigApplicationContext context = new 
AnnotationConfigApplicationContext();

        context.register(MyConfig.class);

        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        UserDao userDao = (UserDao) new MapperSession().getMapper(UserDao.class);
        beanFactory.registerSingleton("userDao", userDao);
        context.refresh();

        context.getBean(UserService.class).getList();
```

+ `FactoryBean` 可行。这里有一个问题。

> FactoryBean和BeanFactory，Bean有什么区别？

BeanFactory是spring中的一个工厂,可以创建bean也可以获取bean。它的职责包括：实例化，定位，配置应用程序中的对象及建立这些对象间的依赖。

FactoryBean实现了FactoryBean接口的Bean，本身是一个bean，getObject()还会返回了一个bean。除此以外还有getObjectType()和isSingleton()（jdk1.8后默认实现）

**自定义MyFactoryBean**

```java
/**
 * @program: handwritngmybits
 * @description:
 * @author: 袁阊越
 * @create: 2019-10-22 15:17
 */
@Component
public class MyFactoryBean implements FactoryBean {

    @Override
    public Object getObject() throws Exception {
        return new MapperSession().getMapper(UserDao.class);
    }

    @Override
    public Class<?> getObjectType() {
        return UserDao.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
```

返回除了自己的对象

```java
  AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        System.out.println(context.getBean("myFactoryBean"));
```

![](https://github.com/yuanchangyue/MybatisSampleAnalysis/blob/master/img/factoryBean%E8%BF%94%E5%9B%9E%E5%AF%B9%E8%B1%A1.png?raw=true)

返回自身加上一个&

```java
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        System.out.println(context.getBean("&myFactoryBean"));
```

![](https://github.com/yuanchangyue/MybatisSampleAnalysis/blob/master/img/factoryBean%E8%BF%94%E5%9B%9E%E8%87%AA%E5%B7%B1.png?raw=true)

**灵活版的MyFactoryBean**

```java
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

```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userDao" class="com.changyue.web.MyFactoryBean">
        <property name="mapperInterface" value="com.changyue.mapper.UserDao"/>
    </bean>

</beans>
```

```java
/**
 * @program: handwritngmybits
 * @description: 配置类
 * @author: 袁阊越
 * @create: 2019-10-22 11:14
 */
@ComponentScan("com.changyue")
@Configuration
@ImportResource("classpath:ApplicationContext.xml")
public class MyConfig {
    
}
```

```java
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        System.out.println(context.getBean("userDao"));
```

![](https://github.com/yuanchangyue/MybatisSampleAnalysis/blob/master/img/factoryBean%E8%BF%94%E5%9B%9E%E8%87%AA%E5%B7%B1.png?raw=true)

**使用FactoryBean自定义后，在xml中及配置生效，可以返回自己本身和代理创建的对象，使用XML配置还是只能注册一个**

> FactoryBean和一般的baen有什么区别？

+  `FactoryMothod`

## 加载多个Mapper给Spring容器 
> 如何加载多个Mapper给Spring?
+ XML 配置只能一个
+ 注解 不行 属性没有办法赋值
+ Spring拓展
  



