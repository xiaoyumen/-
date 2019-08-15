package com.atguigu.lockdemo.utils;

import com.atguigu.lockdemo.bean.User;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

public class CacheUtils {
    private static Map<String, User> map = new HashMap<>();

    public static User getFromCache(String username) {
        User user = map.get(username);
        User user1 = new User();
        BeanUtils.copyProperties(user,user1);
        return user1;
    }

    public static void saveToCache(User user) {
        User put = map.put(user.getUsername(), user);
    }

    public static void removeKey(String key) {
        map.remove(key);
    }
}
