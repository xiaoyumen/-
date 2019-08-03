package com.atguigu.gulimall.pms.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "gulimall-sms")
public interface WorldServer {

    @GetMapping("/world")
    public String hello();
}
