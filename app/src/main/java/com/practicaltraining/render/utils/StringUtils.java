package com.practicaltraining.render.utils;

/**
 * created By LQY
 * 2019.6.24
 * 清除字节数组中的无效数据，并将有效数据转化为字符串
 */
public class StringUtils {
    public static String byteToStr(byte[] buffer) {
        try {
            int length = 0;
            for (int i = 0; i < buffer.length; ++i) {
                if (buffer[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(buffer, 0, length, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
}
