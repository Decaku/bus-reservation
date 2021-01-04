package com.stylefeng.guns.rest.common;

import lombok.Data;

@Data
public class ResponseData<T> {
    private boolean success;

    private String message; // 消息

    private int code;

    private T result; // 返回的数据

    private long timestamp = System.currentTimeMillis(); // 时间戳
}
