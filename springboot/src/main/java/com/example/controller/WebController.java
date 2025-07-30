package com.example.controller;

import com.example.common.Result;
import com.example.common.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.service.AdminService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    private final AdminService adminService = new AdminService();

    private final UserService userService = new UserService();

    /**
     * default request API
     */
    @GetMapping("/")
    public Result hello() {
        return Result.success();
    }

    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            account = adminService.login(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            account = userService.login(account);
        } else {
            return Result.error("Invalid role parameter.");
        }
        return Result.success(account);
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        if (RoleEnum.USER.name().equals(user.getRole())) {
            userService.register(user);
        } else {
            return Result.error("Invalid role parameter.");
        }
        return Result.success();
    }

}
