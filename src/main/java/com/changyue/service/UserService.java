package com.changyue.service;

import com.changyue.mapper.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: handwritngmybits
 * @description: user业务层
 * @author: 袁阊越
 * @create: 2019-10-22 11:26
 */

public class UserService {

    @Autowired
    private UserDao userDao;

    public void getList() {
        System.out.println(userDao.getList());
    }

}
