package com.practicaltraining.render.utils;

import com.practicaltraining.render.core.Node;

/**
 * created By LQY
 * 一些静态变量 如服务器ip地址 端口等
 */
public class StaticVar {
    public static String serverAddress = "211.87.227.71";
    //public static String serverAddress = "192.168.43.148";
    //public static String serverAddress = "10.27.219.145";
    public static String picServerAddress = serverAddress+":8080/img/";
    public static short serverPort = 8888;
    public static Node node = null;
    public static int meshNum = 0;
    public static int currentItemId = -1;
    public static int colorR=0,colorG=0,colorB=0;
    public static int lightR,lightG,lightB;
    public static int currentSecondFrames = 0;
}
