package com.atguigu.lockdemo.controller;

import com.atguigu.lockdemo.service.RedisService;
import org.apache.tomcat.jni.Socket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

@RestController
public class RedisController {

    @Autowired
    RedisService redisService;

    @GetMapping("incr")
    public String incr(HttpServletRequest request){
        String ip = request.getRemoteAddr();
        System.out.println(ip);
        redisService.incr();



        return "ok";
    }
}
