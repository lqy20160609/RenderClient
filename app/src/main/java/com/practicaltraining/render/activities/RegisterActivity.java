package com.practicaltraining.render.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.practicaltraining.render.R;
import com.practicaltraining.render.utils.StaticVar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private static String Address = "http://" + StaticVar.serverAddress + ":8080/RenderLogin/RegisterSer", result = "";
    private EditText nameText, phoneNumberText, passwordText, identiCode;
    private Button signupButton;
    private static String name;
    private static String phoneNumber;
    private ProgressDialog progressDialog;
    private static String password;
    private boolean flag = false;
    private TextView loginLink;
    EventHandler eventHandler = new EventHandler() {
        public void afterEvent(int event, int result, Object data) {
            // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            new Handler(Looper.getMainLooper(), msg1 -> {
                int event1 = msg1.arg1;
                int result1 = msg1.arg2;
                Object data1 = msg1.obj;
                if (event1 == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    if (result1 == SMSSDK.RESULT_COMPLETE) {
                        runOnUiThread(() -> {
                            passwordText.setVisibility(View.VISIBLE);
                            signupButton.setText("注册");
                        });
                    } else {
                        // 处理错误情况
                        ((Throwable) data1).printStackTrace();
                    }
                } else if (event1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (result1 == SMSSDK.RESULT_COMPLETE) {
                        runOnUiThread(()->{
                            progressDialog = new ProgressDialog(RegisterActivity.this,
                                    R.style.AppTheme_Dark_Dialog);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("创建账号中...");
                            progressDialog.show();
                            name = nameText.getText().toString();
                            password = passwordText.getText().toString();
                            RegisterTask mTask = new RegisterTask();
                            mTask.execute();
                        });
                    } else {
                        identiCode.setError("请输入正确的验证码");
                        ((Throwable) data1).printStackTrace();
                    }
                    // 处理其他接口
                }
                return false;
            }).sendMessage(msg);
        }
    };

    private void initView() {
        nameText = findViewById(R.id.input_name_register);
        phoneNumberText = findViewById(R.id.input_phoneNumber_register);
        passwordText = findViewById(R.id.input_password_register);
        signupButton = findViewById(R.id.btn_signup);
        loginLink = findViewById(R.id.link_login);
        identiCode = findViewById(R.id.input_identiCode_register);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        signupButton.setOnClickListener(v -> signup());
        SMSSDK.registerEventHandler(eventHandler);
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
    }

    public void signup() {
        if (signupButton.getText().toString().equals("获取验证码")) {
            String number = phoneNumberText.getText().toString();
            if (!isMobileNO(number)) {
                phoneNumberText.setError("请输入有效的手机号码");
            } else {
                SMSSDK.getVerificationCode("86", number);
                phoneNumber = number;
            }
        } else if (signupButton.getText().toString().equals("注册")) {
            if (validate()) {
                SMSSDK.submitVerificationCode("86", phoneNumber, identiCode.getText().toString());
            }
        }
    }

    //异步任务发出注册申请
    public class RegisterTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                URL ServerUrl = new URL(Address);
                HttpURLConnection conn;
                conn = (HttpURLConnection) ServerUrl.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                JSONObject loginMessage = new JSONObject();
                loginMessage.put("name", name);
                loginMessage.put("password", password);
                loginMessage.put("number", phoneNumber);
                OutputStream os = conn.getOutputStream();
                os.write(String.valueOf(loginMessage).getBytes("UTF-8"));
                os.close();
                if (conn.getResponseCode() == 200) {
                    StringBuffer sb = new StringBuffer();
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    result = new String(sb);
                    Log.d(TAG+"lqy", "result"+result);
                    return 1;
                } else {
                    Log.d(TAG+"lqy", "responseCode"+conn.getResponseCode());
                    return 2;
                }
            } catch (Exception e) {
                Log.d(TAG+"lqy", "hahahahaha");
                e.printStackTrace();
                return 3;
            }

        }

        @Override
        protected void onPostExecute(Object o) {
            if (o.equals(1)) {
                if (result.equals("1")) {
                    if (progressDialog!=null&&progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    onSignupSuccess();
                }else{
                    if (progressDialog!=null&&progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(RegisterActivity.this,"用户已存在",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void onSignupSuccess() {
        Intent intent =new Intent();
        intent.putExtra("number",phoneNumber);
        intent.putExtra("password",password);
        setResult(RESULT_OK, intent);
        finish();
    }

    public boolean validate() {
        boolean valid = true;

        name = nameText.getText().toString();
        String tnumber = phoneNumberText.getText().toString();
        password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("至少三个字符");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (!tnumber.equals(phoneNumber)) {
            phoneNumberText.setError("请输入用于注册的手机号码");
            valid = false;
        } else {
            phoneNumberText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("请输入4-10个字符");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    public static boolean isMobileNO(String mobiles) {
        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}
