package com.example.controller;

import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.entity.AccountFactoryManager;
import com.example.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService = new UserService();

    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        userService.add(user);
        return Result.success(user);
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        userService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result delete(@RequestBody List<Integer> ids) {
        userService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody User user) {
        userService.updateById(user);
        return Result.success(user);
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        User user = userService.selectById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("User not found");
    }

    @GetMapping("/selectAll")
    public Result selectAll(String name) {
        List<User> list = userService.selectAll(name);
        return Result.success(list);
    }

    @GetMapping("/selectPage")
    public Result selectPage(
            String name,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<User> pageInfo = userService.selectPage(name, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/getRole/{id}")
    public Result getUserRole(@PathVariable Integer id) {
        User user = userService.selectById(id);
        if (user != null) {
            return Result.success(user.getRole());
        }
        return Result.error("User not found");
    }

    @PostMapping("/clone/{id}")
    public Result cloneUser(@PathVariable Integer id) {
        User user = userService.selectById(id);
        if (user != null) {
            User clonedUser = user.clone();
            return Result.success(clonedUser);
        }
        return Result.error("User not found");
    }
}
