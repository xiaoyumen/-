package com.atguigu.lockdemo.controller;

import com.atguigu.lockdemo.bean.User;
import com.atguigu.lockdemo.utils.CacheUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {

    @GetMapping("/get")
    public User getUser(@RequestParam("username") String username) {
        User cache = CacheUtils.getFromCache(username);
        if (cache == null) {
            return new User("dbuser", "db@qq.com");
        }
        return cache;
    }

    @GetMapping("/insert")
    public String saveUser(User user) {
        //1.双写模式
        CacheUtils.saveToCache(user);
        //2.给数据库保存
        System.out.println("数据库保存了。。" + user);
        return "ok";
    }
    @GetMapping("/update")
    public User updateUser(String username, String email){
        User user = new User(username, email);
        //1.双写模式
        CacheUtils.saveToCache(new User(username,email));
        //2.更新数据库
        return user;
    }
    @GetMapping("info")
    public String userInfo(String username){
        //1.查缓存
        User cache = CacheUtils.getFromCache(username);
        if (cache==null){
            cache=new User("dbuser","db@qq.com");
        }
        //一堆计算
        cache.setEmail("xxx@qqq.com");
        //该数据库
        return "ok";
    }
}
