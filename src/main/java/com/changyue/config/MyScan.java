package com.changyue.config;

import com.changyue.web.MyImportBeanDefinitionRegister;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @program: handwritngmybits
 * @description:
 * @author: 袁阊越
 * @create: 2019-11-14 15:02
 */
@Retention(RetentionPolicy.RUNTIME)
@Import(MyImportBeanDefinitionRegister.class)
@MapperScan
public @interface MyScan {
    String[] value() default {};
}
