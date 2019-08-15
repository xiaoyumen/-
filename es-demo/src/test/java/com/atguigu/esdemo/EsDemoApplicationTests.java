package com.atguigu.esdemo;

import io.searchbox.client.JestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;


public class EsDemoApplicationTests {


    @Autowired
    JestClient jestClient;
    @Test
    public void contextLoads() {
        ArrayList<Object> objects = new ArrayList<>();


        if (objects!=null){
            System.out.println("不为空");
        }else {
            System.out.println("为空");
        }

    }

}
