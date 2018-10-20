package com.example.slj.gradeadministration;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.*;
import org.json.JSONObject;

import java.net.URL;

public class SecurityActivity extends AppCompatActivity {

    private static final String PRE_URL = "http://139.199.25.59:8080/GradeAdministrationApi/grade/";
    private ProgressDialog progressDialog = null;

    @BindView(R.id.security_toolbar)
    Toolbar toolbar;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_security_u) Button securityButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回键
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        securityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

    }

    public void update() {
        if (!validate()) {
            onUpdateFailed("输入有误");
            return;
        }

        securityButton.setEnabled(false);

        // TODO: Implement your own signup logic here.
        showDialog();
        checkUpdateInfo();
    }


    //注册成功
    public void onUpdateSuccess(final String message) {
        Toast.makeText(getBaseContext(),message,Toast.LENGTH_LONG);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(),message,Toast.LENGTH_LONG);
                        finish();
                    }
                }, 1000);

    }

    //注册失败
    public void onUpdateFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();

        securityButton.setEnabled(true);
    }

    //输入违法
    public boolean validate() {
        boolean valid = true;

        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("字母、数字组成，4-10位");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("两次密码不一致");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    //弹出登录加载框
    public void showDialog(){
        progressDialog = new ProgressDialog(SecurityActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("正在修改...");
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

    //开启线程验证注册
    public void checkUpdateInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String password = _passwordText.getText().toString();
                String sno = getIntent().getStringExtra("sno");
                Response response = null;
                try {
                    URL url = new URL(PRE_URL+"updateLoginInfo.json");
                    OkHttpClient httpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("account",sno)
                            .add("pw",password)
                            .add("identity",String.valueOf(0))
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
                            onUpdateFailed("网络连接失败");
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
                                onUpdateFailed(message);
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeDialog();
                                onUpdateSuccess(message);
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
