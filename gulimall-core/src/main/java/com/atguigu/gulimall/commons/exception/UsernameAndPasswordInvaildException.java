package com.atguigu.gulimall.commons.exception;

public class UsernameAndPasswordInvaildException extends RuntimeException {

    public UsernameAndPasswordInvaildException() {
        super("账号密码不对");
    }
}
