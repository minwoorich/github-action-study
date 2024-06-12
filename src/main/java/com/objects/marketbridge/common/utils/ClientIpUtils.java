package com.objects.marketbridge.common.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

public class ClientIpUtils {
    private final static String[] headerTypes = {"X-Forwarded-For", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

    public static Map<String, String> getClientIps(HttpServletRequest request) {
        Map<String, String> ipMap = new HashMap<>();

        for (String headerType : headerTypes) {
            String ip = request.getHeader(headerType);
            if (ip != null) {
                ipMap.put(headerType, ip);
            }else{
                ipMap.put(headerType, "");
            }
        }
        ipMap.put("Remote-IP", request.getRemoteAddr());
        return ipMap;
    }
}
