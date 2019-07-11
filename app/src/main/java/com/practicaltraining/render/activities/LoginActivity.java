package com.practicaltraining.render.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.practicaltraining.render.MainActivity;
import com.practicaltraining.render.R;
import com.practicaltraining.render.utils.StaticVar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static String Address = "http://" + StaticVar.serverAddress + ":8080/RenderLogin/loginSer", result = "";
    private String phoneNumber,password;
    private Button skip;
    private EditText phoneNumberText;
    private EditText passwordText;
    private Button loginButton;
    private TextView signupLink;
    private ProgressDialog progressDialog;

    private void initView(){
        phoneNumberText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.btn_login);
        signupLink = findViewById(R.id.link_signup);
        skip = findViewById(R.id.skip);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        skip.setOnClickListener(v->{
            onLoginSuccess();
        });
        loginButton.setOnClickListener(v -> login());
        signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivityForResult(intent, REQUEST_SIGNUP);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
        SharedPreferences sharedPreferences= getSharedPreferences("user_information",Context.MODE_PRIVATE);
        String s1 = sharedPreferences.getString("number","");
        String s2 = sharedPreferences.getString("password","");
        phoneNumberText.setText(s1);
        passwordText.setText(s2);

    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("登陆中...");
        progressDialog.show();

        phoneNumber  = phoneNumberText.getText().toString();
        password = passwordText.getText().toString();
        LoginTask mTask = new LoginTask();
        mTask.execute();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                phoneNumberText.setText(data.getStringExtra("number"));
                passwordText.setText(data.getStringExtra("password"));
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                //this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        startActivity(new Intent(this,MainActivity.class));
        SharedPreferences sharedPreferences= getSharedPreferences("user_information",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("number",phoneNumber);
        editor.putString("password", password);
        editor.apply();
        finish();
    }
    public static boolean isMobileNO(String mobiles) {
        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "登陆失败", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String number = phoneNumberText.getText().toString();
        String password = passwordText.getText().toString();

        if (number.isEmpty() || !isMobileNO(number)) {
            phoneNumberText.setError("请输入合法的手机号码");
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
    //异步任务发出注册申请
    public class LoginTask extends AsyncTask {
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
                loginMessage.put("number", phoneNumber);
                loginMessage.put("password", password);
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
                    Log.d(TAG+"lqy", ""+conn.getResponseCode());
                    return 2;
                }
            } catch (Exception e) {
                Log.d(TAG+"lqy", "hahahahah");
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
                    onLoginSuccess();
                }else{
                    if (progressDialog!=null&&progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    SharedPreferences sharedPreferences= getSharedPreferences("user_information",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("number","");
                    editor.putString("password", "");
                    editor.apply();
                    loginButton.setEnabled(true);
                    Toast.makeText(LoginActivity.this,"账号密码错误",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
