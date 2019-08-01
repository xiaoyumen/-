package com.atguigu.gulimall.sms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.atguigu.gulimall.sms.dao")
@SpringBootApplication
public class GullmallSmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GullmallSmsApplication.class, args);
    }

}
