package com.example.slj.gradeadministration;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.*;
import org.json.JSONObject;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String PRE_URL = "http://139.199.25.59:8080/GradeAdministrationApi/grade/";
    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog progressDialog = null;

    @BindView(R.id.input_sno) EditText _snoText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    //登录
    public void login() {
        if (!validate()) {
            onLoginFailed("输入信息有误");
            return;
        }

        _loginButton.setEnabled(false);

        showDialog();
        // TODO: Implement your own authentication logic here.
        checkLogin();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    //按返回键
    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    //登录成功
    public void onLoginSuccess(final String message) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        Intent intent = new Intent(LoginActivity.this,GradeActivity.class);
                        String sno = _snoText.getText().toString();
                        intent.putExtra("sno",sno);
                        startActivity(intent);
                        finish();
                    }
                }, 1500);

    }

    //登录失败
    public void onLoginFailed(String fail_message) {
        Toast.makeText(getBaseContext(), fail_message, Toast.LENGTH_SHORT).show();

        _loginButton.setEnabled(true);
    }

    //输入违法
    public boolean validate() {
        boolean valid = true;

        String sno = _snoText.getText().toString();
        String password = _passwordText.getText().toString();

        if (sno.isEmpty() || sno.length() != 10) {
            _snoText.setError("学号格式不正确");
            valid = false;
        } else {
            _snoText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("字母、数字组成，4-10位");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    //弹出登录加载框
    public void showDialog(){
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("正在登录...");
        progressDialog.show();
    }

    //关闭登录加载框
    public void closeDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                progressDialog.dismiss();
                            }
                        }, 1000);
            }
        });

    }

    //开启线程验证登录信息
    public void checkLogin(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sno = _snoText.getText().toString();
                String password = _passwordText.getText().toString();
                Response response = null;
                try {
                    URL url = new URL(PRE_URL+"getLogin.json");
                    OkHttpClient httpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("account",sno)
                            .add("password",password)
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    response = httpClient.newCall(request).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeDialog();
                            onLoginFailed("网络连接失败");
                            return;
                        }
                    });

                }

                try {
                    String resultJson = response.body().string();
                    System.out.print(resultJson);
                    JSONObject jsonObject = null;
                    jsonObject = new JSONObject(resultJson);
                    boolean login_result = jsonObject.getBoolean("success");
                    final String message = jsonObject.getString("message");
                    if (!login_result){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeDialog();
                                onLoginFailed(message);
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeDialog();
                                onLoginSuccess(message);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}