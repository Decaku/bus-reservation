package com.stylefeng.guns.rest.common;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

public class CurrentUser {

    private static final ConcurrentHashMap<String, String> concurrentHashMap= new ConcurrentHashMap<>();

    public static void saveUserIdAndToken(String userId, String token) {
        concurrentHashMap.put(userId, token);
    }

    public static String getTokenByUserId(String userId) {
        return concurrentHashMap.get(userId);
    }

    public static void deleteUserId(String userId) {
        concurrentHashMap.remove(userId);
    }

    // redis绑定
    public static String getToken(HttpServletRequest request) {
        final String requestHeader = request.getHeader("Authorization");
        String authToken = requestHeader.substring(7);
        return authToken;
    }
}
