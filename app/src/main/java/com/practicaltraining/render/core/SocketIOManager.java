package com.practicaltraining.render.core;

import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.callbacks.CreatedModelFinished;
import com.practicaltraining.render.callbacks.GetModelPhotoCompleted;
import com.practicaltraining.render.callbacks.GetRoamingPhotoCompleted;
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
    private static String TAG = "SocketIOManager";
    private volatile static SocketIOManager socketIOManagerInstance;
    private Socket socket;
    private BufferedInputStream bis;
    private PrintWriter printWriter;
    private String result = "";
    private StringBuilder sb = new StringBuilder();
    private GetModelPhotoCompleted modelFinishCallBack;
    private GetRoamingPhotoCompleted roamingPhotoCompleted;
    private CreatedModelFinished createdModelFinished;

    public void setRoamingPhotoCompleted(GetRoamingPhotoCompleted roamingPhotoCompleted) {
        this.roamingPhotoCompleted = roamingPhotoCompleted;
    }

    public void setCreatedModelFinished(CreatedModelFinished createdModelFinished) {
        this.createdModelFinished = createdModelFinished;
    }

    private SocketIOManager() {
        new Thread(()-> {
            try {
                socket = new Socket(StaticVar.serverAddress, StaticVar.serverPort);
                socket.setKeepAlive(true);
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream(), "UTF-8")), true);
                bis = new BufferedInputStream(socket.getInputStream());
            } catch (IOException e) {
                Log.d(TAG+" constructor","连接失败");
                e.printStackTrace();
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

    public void setModelFinishCallBack(GetModelPhotoCompleted modelFinishCallBack) {
        this.modelFinishCallBack = modelFinishCallBack;
    }

    public void getNewModelScence(JSONObject json) {
        if (socketIOManagerInstance!=null) {
            GetNewModelSceneTask mTask = new GetNewModelSceneTask(json);
            mTask.execute();
        }
    }

    public void getNewRoamingScence(JSONObject json){
        if (socketIOManagerInstance!=null) {
            GetNewRoamingSceneTask mTask = new GetNewRoamingSceneTask(json);
            mTask.execute();
        }
    }

    public void sendParam(JSONObject json){
        if (socketIOManagerInstance!=null) {
            SendParamTask mTask = new SendParamTask(json);
            mTask.execute();
        }
    }

    public void sendParamWithBack(JSONObject json){
        if (socketIOManagerInstance!=null) {
            SendParamWithBackTask mTask = new SendParamWithBackTask(json);
            mTask.execute();
        }
    }

    private class GetNewModelSceneTask extends AsyncTask {
        private JSONObject json;

        public GetNewModelSceneTask(JSONObject json) {
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
                    Log.d(TAG+" pic address", result+"");
                    if (bis.available() <= 0) {
                        break;
                    }
                }
                return 1;
            } catch (Exception e) {
                Log.d(TAG, "发送异常");
                e.printStackTrace();
                return 0;
            }
        }

        @Override
            protected void onPostExecute(Object o) {
                if (o.toString().equals("1")) {
                    modelFinishCallBack.getModelDataCompleted(sb.toString());
                    if (createdModelFinished!=null) {
                        createdModelFinished.onCreatedFinished();
                    }
                }
        }
    }

    private class GetNewRoamingSceneTask extends AsyncTask {
        private JSONObject json;

        public GetNewRoamingSceneTask(JSONObject json) {
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
                    Log.d(TAG+" pic address", result+"");
                    if (bis.available() <= 0) {
                        break;
                    }
                }
                return 1;
            } catch (Exception e) {
                Log.d(TAG, "发送异常");
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o.toString().equals("1")) {
                roamingPhotoCompleted.getRoamingDataCompleted(sb.toString());
            }
        }
    }


    private class SendParamTask extends AsyncTask {
        private JSONObject json;

        public SendParamTask(JSONObject json) {
            this.json = json;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            sb = new StringBuilder();
            try {
                printWriter.println(json.toJSONString());
                return 1;
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o.toString().equals("1")) {
                Log.d(TAG, sb.toString());
            }
        }
    }
    private class SendParamWithBackTask extends AsyncTask {
        private JSONObject json;

        public SendParamWithBackTask(JSONObject json) {
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
                    Log.d(TAG+" paramBack", result+"");
                    if (bis.available() <= 0) {
                        break;
                    }
                }
                return 1;
            } catch (Exception e) {
                Log.d(TAG, "发送异常");
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o.toString().equals("1")) {
                if (sb.toString().equals("finish")){
                    createdModelFinished.onCreatedFinished();
                }
            }
        }
    }
}
