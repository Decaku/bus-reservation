package com.stylefeng.guns.rest.myutils;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class UUIDUtils {

    public static Snowflake snowflake = IdUtil.createSnowflake(1, 1);  // 参数是实例id和数据中心id，参考雪花算法

    public static Long flakesUUID() {
        return snowflake.nextId();
    }
}
