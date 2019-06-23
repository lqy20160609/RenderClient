package com.practicaltraining.render.socketio;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.callbacks.GetPhotoCompleted;
import com.practicaltraining.render.utils.StaticVar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private static BufferedReader br;
    private static PrintWriter printWriter;
    private static String result = "";
    private static StringBuilder sb = new StringBuilder();
    private static GetPhotoCompleted finishcallback;

    private SocketIOManager() throws IOException {
        socket = new Socket(StaticVar.serverAddress, StaticVar.serverPort);
        printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream(), "UTF-8")), true);
        br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    }

    public static SocketIOManager getInstance() {
        if (socketIOManagerInstance == null) {
            synchronized (SocketIOManager.class) {
                if (socketIOManagerInstance == null) {
                    try {
                        socketIOManagerInstance = new SocketIOManager();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    return socketIOManagerInstance;
                }
            }
        }
        return socketIOManagerInstance;
    }

    public void setFinishcallback(GetPhotoCompleted finishcallback) {
        this.finishcallback = finishcallback;
    }

    public void getNewScence() {
        GetNewSceneTask mTask = new GetNewSceneTask();
        mTask.execute();
    }

    private static class GetNewSceneTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                sb = new StringBuilder();
                JSONObject json = new JSONObject();
                json.put("render", 1);
                json.put("renderType", "Optix");
                printWriter.println(json.toJSONString());
                while ((result = br.readLine()) != null) {
                    sb.append(result);
                }
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o.equals(1)) {
                finishcallback.getDataCompleted(sb.toString());
            }
        }
    }
}
