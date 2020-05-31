package com.cloud.service.utils;

import org.apache.commons.lang.RandomStringUtils;

public class UUIDUtil {

    public UUIDUtil() {
    }

    /**
     * 指定头部+随机生成指定位数(num)的字符串
     *
     * @param letter
     * @return
     */
    public static String getUNIDX(String letter, int num) {
        return (letter + RandomStringUtils.randomAlphanumeric(num));
    }

}
