package com.project.platform.controller;

import com.project.platform.entity.User;
import com.project.platform.service.UserService;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // @Controller+@Response
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("list")
    public ResponseVO<List<User>> list(){
        return ResponseVO.ok(userService.list());
    }

    /**
     * Query by user id
     * @param id
     * @return
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<List<User>> selectById(@PathVariable("id") Integer id){
        return ResponseVO.ok(userService.selectById(id));
    }

    /**
     * Query by username
     * @param username
     * @return
     */
    @GetMapping("selectByUsername/{username}")
    public ResponseVO<List<User>> selectByUsername(@PathVariable("username") String username){
        return ResponseVO.ok(userService.selectByUsername(username));
    }

    /**
     * Query by phone number
     * @param tel
     * @return
     */
    @GetMapping("selectByTel/{tel}")
    public ResponseVO<List<User>> selectByTel(@PathVariable("tel") String tel){
        return ResponseVO.ok(userService.selectByTel(tel));
    }

    /**
     * insert(add) new user
     * @param newUser
     * @return
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody User newUser){
        userService.insert(newUser);
        return ResponseVO.ok();
    }
}
