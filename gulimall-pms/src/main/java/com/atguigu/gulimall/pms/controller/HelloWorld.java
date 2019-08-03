package com.atguigu.gulimall.pms.controller;

import com.atguigu.gulimall.pms.feign.WorldServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RefreshScope
@RestController
public class HelloWorld {

    @Autowired
    WorldServer worldServer;
    @Value("${my.content}")
    private String content="";
    @Value("${datasource}")
    private String redisurl="";
    @GetMapping("/hello")
    public String testfeign(){
        String msg ="";
        msg = worldServer.hello();
        return "hello"+msg+""+content+""+redisurl;
    }
}
