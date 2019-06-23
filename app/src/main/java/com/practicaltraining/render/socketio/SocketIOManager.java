package com.practicaltraining.render.socketio;

/**
 * created By LQY
 * 2019.6.23
 * socket通信管理 单例模式 保持链接 异步接收数据
 */
public class SocketIOManager {
    private volatile static SocketIOManager socketIOManagerInstance;
    private SocketIOManager() {

    }

    public static SocketIOManager getInstance() {
        if (socketIOManagerInstance == null) {
            synchronized (SocketIOManager.class) {
                if (socketIOManagerInstance == null) {
                    socketIOManagerInstance = new SocketIOManager();
                    return socketIOManagerInstance;
                }
            }
        }
        return socketIOManagerInstance;
    }
}
