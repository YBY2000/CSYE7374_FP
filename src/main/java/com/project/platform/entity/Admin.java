package com.project.platform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Admin info list
 */
@Data
public class Admin {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String tel;
    private String email;
    private String status;
    private LocalDateTime createTime;
}
