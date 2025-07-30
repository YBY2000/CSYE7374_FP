package com.example.controller;

import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.entity.AccountFactoryManager;
import com.example.service.UserFactoryService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/userManagement")
public class UserManagementController {

    @Autowired
    private UserFactoryService userFactoryService;
    
    private final UserService userService = new UserService();

    @PostMapping("/createUser")
    public Result createUser(@RequestParam String role, 
                           @RequestParam String name) {
        Account account = AccountFactoryManager.createUser(role, name);
        if (account instanceof User) {
            return Result.success((User) account);
        } else if (account instanceof Admin) {
            return Result.success((Admin) account);
        }
        return Result.success(account);
    }

    @PostMapping("/cloneUser")
    public Result cloneUser(@RequestBody User user) {
        User clonedUser = userFactoryService.cloneUser(user);
        return Result.success(clonedUser);
    }

    @PostMapping("/cloneUsers")
    public Result cloneUsers(@RequestBody List<User> users) {
        List<User> clonedUsers = userFactoryService.cloneUsers(users);
        return Result.success(clonedUsers);
    }

    @GetMapping("/getUserByRole/{role}")
    public Result getUserByRole(@PathVariable String role) {
        Account account = userFactoryService.createUserByRole(role);
        if (account instanceof User) {
            return Result.success((User) account);
        } else if (account instanceof Admin) {
            return Result.success((Admin) account);
        }
        return Result.success(account);
    }

    @GetMapping("/getUserRole/{id}")
    public Result getUserRole(@PathVariable Integer id) {
        User user = userService.selectById(id);
        if (user != null) {
            return Result.success(user.getRole());
        }
        return Result.error("User not found");
    }

    @PostMapping("/createNormalUser")
    public Result createNormalUser() {
        Account account = userFactoryService.createNormalUser();
        if (account instanceof User) {
            return Result.success((User) account);
        }
        return Result.success(account);
    }

    @PostMapping("/createAdminUser")
    public Result createAdminUser() {
        Account account = userFactoryService.createAdminUser();
        if (account instanceof Admin) {
            return Result.success((Admin) account);
        }
        return Result.success(account);
    }

    @GetMapping("/getAllUsers")
    public Result getAllUsers(String name) {
        List<User> users = userService.selectAll(name);
        return Result.success(users);
    }
}
