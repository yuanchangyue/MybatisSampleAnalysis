package com.changyue.mapper;

import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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
