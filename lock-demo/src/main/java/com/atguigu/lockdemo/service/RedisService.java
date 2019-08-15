package com.atguigu.lockdemo.service;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    public  void incr() {
        //1.获取锁 setnx;占锁和设置超时应该是原子的
        String token = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS);
        if (lock){
            //设置超时
            System.out.println("获取到锁。。。。");
            //1.1获取Redis中原来的值是多少
            String num = redisTemplate.opsForValue().get("num");
            Integer i = Integer.parseInt(num);
            i++;
            //1.2 把新加的值放进去
            redisTemplate.opsForValue().set("num",i.toString());
            //2.删除锁；我们直接删除锁是很危险。如果业务超时，锁自动过期，就会导致别人获取到锁，
            //如果再来删除，删的是别人的锁
            //删除必须是原子的
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object o = redisTemplate.execute(new DefaultRedisScript<>(script), Arrays.asList("lock"), token);
            System.out.println("删锁。。。。"+o.toString());
            //自动续期：超级麻烦
            //redisson:分布式锁&分布式集合 Map


        }else {
             try {
                 System.out.println("没获取到，等待重试");

             } catch (Exception e) {
                         e.printStackTrace();
                     }
        }




    }

    public  void  incr2()throws InterruptedException{
      RLock lock =  redissonClient.getLock("lock");//只要锁名字一样就是同一把锁
        boolean b = lock.tryLock(100, 10, TimeUnit.SECONDS);
        if (b){
            System.out.println("redisson锁住了。。。。");
            String num = redisTemplate.opsForValue().get("num");
            Integer i = Integer.parseInt(num);
            i++;
            //1.2把新加的值放进去
            redisTemplate.opsForValue().set("num",i.toString());
        }
        System.out.println("redisson释放锁。。");
    }
    public String read() throws InterruptedException{
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("data");
        RLock lock = readWriteLock.readLock();
        lock.lock();
        String hello =redisTemplate.opsForValue().get("hello");
        lock.unlock();
        return hello;
    }
    public String write()throws  InterruptedException{
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("data");
        RLock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        Thread.sleep(3000);
        redisTemplate.opsForValue().set("hello",UUID.randomUUID().toString());
        writeLock.unlock();
        return "ok";
    }

}
