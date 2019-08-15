package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.es.EsSkuVo;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/es")
public class SpuToEsController {

    @Autowired
    JestClient jestClient;

    /**
     * 商品上架
     * @return
     */
    @PostMapping("/spu/up")
    public Resp<Object> spuUp(@RequestBody List<EsSkuVo> vo){
        if (vo!=null&&vo.size()>0) {
            vo.forEach(vo1 -> {
                Index index = new Index.Builder(vo1)
                        .index(Constant.ES_SPU_INDEX)
                        .type(Constant.ES_SPU_TYPE)
                        .id(vo1.getId().toString())
                        .build();
                try {
                    jestClient.execute(index);
                } catch (Exception e) {

                }

            });
        }
        System.out.println("es数据准备发送");
        return Resp.ok(null);
    }
}
