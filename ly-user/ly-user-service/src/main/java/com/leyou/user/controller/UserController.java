package com.leyou.user.controller;


import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> check(@PathVariable("data") String data,@PathVariable("type") Integer type){
        Boolean boo= userService.checkData(data,type);
        if(boo==null){

            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return   ResponseEntity.ok(boo);

    }

    @PostMapping("code")
    public  ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone){
        //186发验证码

        //验证码保存
       Boolean b= userService.sendVerifyCode(phone);

        if(b==null){

            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return  new  ResponseEntity<>(HttpStatus.CREATED);

    }

    @PostMapping("register")
    public ResponseEntity<Void> createUser(@Valid User user, @RequestParam("code") String code){
       Boolean b= userService.register(user,code);
        if(b==null){

            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return  new  ResponseEntity<>(HttpStatus.CREATED);
    }


    @PostMapping("login")
    public  ResponseEntity<User> queryUser(@RequestParam("username") String username,@RequestParam("password") String password){
       User user= userService.queryUser(username,password);
        if(user!=null){

            return   ResponseEntity.ok(user);
        }

        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
