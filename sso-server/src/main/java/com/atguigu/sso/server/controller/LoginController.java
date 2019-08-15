package com.atguigu.sso.server.controller;

import com.atguigu.sso.server.util.GuliJwtUtils;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.UUID;

@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String loginPage(@RequestParam(value = "redirect_url",required = false)String redirectUrl, Model model,
                            @CookieValue(value = "atguigusso",required = false) String sso){
        //判断之前是否已经有其他系统登录过给浏览器留下cookie了；
        if (StringUtils.isEmpty(sso)){
            //没有人登录过
            if (!StringUtils.isEmpty(redirectUrl)){
                model.addAttribute("url",redirectUrl);
            }
            return "login";
        }else {
            //之前有人登录过了，调用自己哪里，我会额外给你加参数
            return "redirect:"+redirectUrl+"?atguigusso="+sso;
        }


    }
    @PostMapping("/doLogin")
    public String doLogin(String username, String password, String url, Model model, HttpServletResponse response){
        System.out.println("提交的数据"+username+"==>"+password+"url");
        //登录
        if (!StringUtils.isEmpty(username)&&!StringUtils.isEmpty(password)){
            //登录成功。。。
            //保存到了Redis；key=username;UUID
            //支持本地验证；2.jwt自带负载信息
            String token = UUID.randomUUID().toString().substring(0, 5);
            /**
             * jwt的负载信息
             */
            HashMap<String, Object> loginUser = new HashMap<>();
            loginUser.put("name",username);
            loginUser.put("email",username+"@qq.com");
            loginUser.put("token",token);
            //做成一个jwt
            DefaultClaims claims = new DefaultClaims();
            GuliJwtUtils.buildJwt(loginUser,claims)
        }

        return null;
    }
}
