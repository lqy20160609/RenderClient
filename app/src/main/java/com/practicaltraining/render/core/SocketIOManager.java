package com.practicaltraining.render.core;

import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.callbacks.GetPhotoCompleted;
import com.practicaltraining.render.utils.StaticVar;
import com.practicaltraining.render.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * created By LQY
 * 2019.6.23
 * socket通信管理 单例模式 保持链接 异步发送接收数据
 */
public class SocketIOManager {
    private volatile static SocketIOManager socketIOManagerInstance;
    private Socket socket;
    private BufferedInputStream bis;
    private PrintWriter printWriter;
    private String result = "";
    private StringBuilder sb = new StringBuilder();
    public GetPhotoCompleted finishcallback;

    private SocketIOManager() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(StaticVar.serverAddress, StaticVar.serverPort);
                    socket.setKeepAlive(true);
                    printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream(), "UTF-8")), true);
                    bis = new BufferedInputStream(socket.getInputStream());
                } catch (IOException e) {
                    Log.d("lqyDebug","连接失败");
                    e.printStackTrace();
                }
            }
        }).start();

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

    public void setFinishcallback(GetPhotoCompleted finishcallback) {
        this.finishcallback = finishcallback;
    }

    public void getNewScence(JSONObject json) {
        GetNewSceneTask mTask = new GetNewSceneTask(json);
        mTask.execute();
    }

    private class GetNewSceneTask extends AsyncTask {
        private JSONObject json;

        public GetNewSceneTask(JSONObject json) {
            this.json = json;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                sb = new StringBuilder();
                printWriter.println(json.toJSONString());
                byte buff[] = new byte[1024];
                while (bis.read(buff, 0, 1024) != -1) {
                    result=StringUtils.byteToStr(buff);
                    sb.append(result);
                    Log.d("lqyDeBug pic address", result+"");
                    if (bis.available() <= 0) {
                        break;
                    }
                }
                return 1;
            } catch (IOException e) {
                Log.d("lqyDeBug exception", "发送异常");
                e.printStackTrace();
                return 0;
            }
        }

        @Override
            protected void onPostExecute(Object o) {
                if (o.toString().equals("1")) {
                    Log.d("lqyDeBug onPostExecute", sb.toString());
                    finishcallback.getDataCompleted(sb.toString());
                }
        }
    }
}
