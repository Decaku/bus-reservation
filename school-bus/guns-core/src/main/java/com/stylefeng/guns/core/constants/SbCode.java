package com.stylefeng.guns.core.constants;

/**
 * 状态码和信息
 */
public enum SbCode {

    USERNAME_ALREADY_EXISTS("003003", "用户名已存在"),
    USERNAME_ALREADY_NO_EXISTS("003009", "用户名不存在");


    private String code;
    private String msg;

    SbCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
