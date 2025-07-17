package com.project.platform.mapper;

import com.project.platform.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {
    /**
     * Query all user
     * @return user list
     */
    @Select("SELECT * FROM user")
    List<User> list();


    /**
     * Query by username
     * @param username
     */
    @Select("SELECT * FROM user where username = #{username}")
//    @Select("SELECT * FROM user where username LIKE CONCAT('%',#{username},'%')")
    User selectByUsername(String username);

    /**
     * Query by user id
     * @param id
     */
    @Select("SELECT * FROM user where id = #{id}")
    User selectById(Integer id);

    /**
     * Query by user phone number
     *
     * @param tel
     */
    @Select("SELECT * FROM user where tel = #{tel}")
    User selectByTel(String tel);

    /**
     * add new user
     * @param user
     * @return
     */
    int insert(User user);
}
