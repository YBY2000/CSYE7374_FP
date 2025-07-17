package com.project.platform.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.RetrievePasswordDTO;
import com.project.platform.dto.UpdatePasswordDTO;
import com.project.platform.entity.User;
import com.project.platform.mapper.UserMapper;
import com.project.platform.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public CurrentUserDTO login(String username, String password) {
        return null;
    }

    @Override
    public void register(JSONObject data) {

    }

    @Override
    public void updateCurrentUserInfo(CurrentUserDTO currentUserDTO) {

    }

    @Override
    public void updateCurrentUserPassword(UpdatePasswordDTO updatePassword) {

    }

    @Override
    public void resetPassword(Integer id) {

    }

    @Override
    public void retrievePassword(RetrievePasswordDTO retrievePasswordDTO) {

    }


    @Override
    public List<User> list() {
        return userMapper.list();
    }

    @Override
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public User selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User selectByTel(String tel) {
        return userMapper.selectByTel(tel);
    }

}
