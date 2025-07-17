package com.project.platform.service;

import com.project.platform.entity.User;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

public interface UserService extends CommonService{
    /**
     * return list
     */
    List<User> list();

    /**
     * Query user by id
     * @param id
     * @return
     */
    User selectById(Integer id);

    /**
     * Fuzzy query user by username
     * @param username
     * @return
     */
    User selectByUsername(String username);

    /**
     * Query user by phone number
     * @param tel
     * @return
     */
    User selectByTel(String tel);
    
}
