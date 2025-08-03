package com.example.controller;

import com.example.common.Result;
import com.example.common.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.LoginRequest;
import com.example.entity.AccountFactoryManager;
import com.example.entity.User;
import com.example.service.AdminService;
import com.example.service.UserService;
import com.example.service.UserFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    private final AdminService adminService = new AdminService();
    private final UserService userService = new UserService();
    
    @Autowired
    private UserFactoryService userFactoryService;

    @GetMapping("/")
    public Result hello() {
        return Result.success();
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest) {
        Account loginAccount = userFactoryService.createUserByRole(loginRequest.getRole());
        loginAccount.setUsername(loginRequest.getUsername());
        loginAccount.setPassword(loginRequest.getPassword());
        loginAccount.setRole(loginRequest.getRole());
        
        if (RoleEnum.ADMIN.name().equals(loginRequest.getRole())) {
            Account resultAccount = adminService.login(loginAccount);
            if (resultAccount instanceof Admin) {
                return Result.success((Admin) resultAccount);
            }
            return Result.success(resultAccount);
        } else if (RoleEnum.USER.name().equals(loginRequest.getRole())) {
            Account resultAccount = userService.login(loginAccount);
            if (resultAccount instanceof User) {
                return Result.success((User) resultAccount);
            }
            return Result.success(resultAccount);
        } else {
            return Result.error("Invalid role parameter.");
        }
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        if (RoleEnum.USER.name().equals(user.getRole())) {
            userService.register(user);
        } else if (RoleEnum.ADMIN.name().equals(user.getRole())) {
            Account adminAccount = userFactoryService.createAdminUser();
            adminAccount.setUsername(user.getUsername());
            adminAccount.setPassword(user.getPassword());
            adminAccount.setName(user.getName());
            adminAccount.setRole(user.getRole());
            // 这里可以调用 adminService.register() 如果存在的话
            return Result.success(adminAccount);
        } else {
            return Result.error("Invalid role parameter.");
        }
        return Result.success();
    }
}
