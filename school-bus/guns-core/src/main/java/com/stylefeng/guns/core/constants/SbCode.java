package com.stylefeng.guns.core.constants;

/**
 * 状态码和信息
 */
public enum SbCode {
    SUCCESS("000000", "成功"),
    USERNAME_ALREADY_EXISTS("003003", "用户名已存在"),
    USERNAME_ALREADY_NO_EXISTS("003009", "用户名不存在"),
    USER_REGISTER_VERIFY_FAILED("003007", "用户注册失败插入验证数据失败"),
    USERORPASSWORD_ERRROR("003001", "用户名或密码不正确"),
    DB_EXCEPTION("003097", "数据库异常"),
    USER_INFOR_INVALID("003008", "用户信息不合法"),

    SELECTED_SEATS("004000", "座位已被选择，请重新选择座位"),
    SYSTEM_ERROR("003099", "系统错误"),

    PAY_PASSWORD_ERROR("004001", "支付密码错误"),
    MONEY_ERROR("004002", "用户余额不足，请充值"),

    REQUISITE_PARAMETER_NOT_EXIST("003073", "必要的参数不能为空"),




    ;



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
