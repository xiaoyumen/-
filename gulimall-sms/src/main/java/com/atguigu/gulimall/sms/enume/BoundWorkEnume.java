package com.atguigu.gulimall.sms.enume;

public enum BoundWorkEnume {
   ALLNO(0,"任何情况都无优惠") ,
    ALL(1,"成长积分无论如何都送");



    private Integer code;
    private String mag;

    BoundWorkEnume(Integer code, String mag) {
        this.code = code;
        this.mag = mag;
    }

    public Integer getCode() {
        return code;
    }

    public String getMag() {
        return mag;
    }
}
