package com.project.platform.service;

import com.alibaba.fastjson2.JSONObject;
import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.RetrievePasswordDTO;
import com.project.platform.dto.UpdatePasswordDTO;

import java.util.Map;

public interface CommonService<T> {
    /**
     * login
     * @param username
     * @param password
     * @return
     */
    CurrentUserDTO login(String username, String password);

    /**
     * register
     * @param data
     */

    void register(JSONObject data);

    /**
     * update current user info
     * @param currentUserDTO
     */

    void updateCurrentUserInfo(CurrentUserDTO currentUserDTO);

    /**
     * update current user pwd
     * @param updatePassword
     */

    void updateCurrentUserPassword(UpdatePasswordDTO updatePassword);

    /**
     * reset pwd
     * @param id
     */
    void resetPassword(Integer id);

    /**
     * forget pwd
     * @param retrievePasswordDTO
     */

    void retrievePassword(RetrievePasswordDTO retrievePasswordDTO);

    /**
     * query current user info by id
     * @param id
     * @return
     */

    T selectById(Integer id);

}
