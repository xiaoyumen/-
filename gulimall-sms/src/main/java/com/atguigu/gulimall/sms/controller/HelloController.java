package com.atguigu.gulimall.sms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

@GetMapping("/world")
  public String hello(){
    return "world";
}
}
