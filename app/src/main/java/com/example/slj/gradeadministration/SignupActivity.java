package com.example.slj.gradeadministration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
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

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static final String PRE_URL = "http://139.199.25.59:8080/GradeAdministrationApi/grade/";
    private ProgressDialog progressDialog = null;

    @BindView(R.id.input_sno) EditText _snoText;
    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_sex) EditText _sexText;
    @BindView(R.id.input_age) EditText _ageText;
    @BindView(R.id.input_class) EditText _classText;
    @BindView(R.id.input_dept) EditText _deptText;
    @BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("输入有误");
            return;
        }

        _signupButton.setEnabled(false);

        // TODO: Implement your own signup logic here.
        showDialog();
        checkSignUp();
    }


    //注册成功
    public void onSignupSuccess(final String message) {
        Toast.makeText(getBaseContext(),message,Toast.LENGTH_LONG);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1000);

    }

    //注册失败
    public void onSignupFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();

        _signupButton.setEnabled(true);
    }

    //输入违法
    public boolean validate() {
        boolean valid = true;

        String sno = _snoText.getText().toString();
        String name = _nameText.getText().toString();
        String sex = _sexText.getText().toString();
        String age = _ageText.getText().toString();
        String classNo = _classText.getText().toString();
        String dept = _deptText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (sno.isEmpty() || sno.length() != 10) {
            _snoText.setError("学号格式不正确");
        }
        if (name.isEmpty() || name.length() < 2) {
            _nameText.setError("姓名格式不正确");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (sex.isEmpty()) {
            _sexText.setError("性别不能为空");
            valid = false;
        } else {
            _sexText.setError(null);
        }


        if (age.isEmpty() || age.length() > 3) {
            _ageText.setError("年龄格式不正确");
            valid = false;
        } else {
            _ageText.setError(null);
        }

        if (classNo.isEmpty() || classNo.length() != 8) {
            _classText.setError("班级格式不正确");
            valid = false;
        } else {
            _classText.setError(null);
        }

        if (dept.isEmpty()) {
            _deptText.setError("学院不能为空");
            valid = false;
        } else {
            _deptText.setError(null);
        }

        if (mobile.isEmpty() || !Patterns.PHONE.matcher(mobile).matches()) {
            _mobileText.setError("请输入正确的手机号码");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

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
        progressDialog = new ProgressDialog(SignupActivity.this,
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

    //开启线程验证注册
    public void checkSignUp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sno = _snoText.getText().toString();
                String name = _nameText.getText().toString();
                String sex = _sexText.getText().toString();
                String age = _ageText.getText().toString();
                String classNo = _classText.getText().toString();
                String dept = _deptText.getText().toString();
                String mobile = _mobileText.getText().toString();
                String password = _passwordText.getText().toString();

                Response response = null;
                try {
                    URL url = new URL(PRE_URL+"getSignUp.json");
                    OkHttpClient httpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("sno",sno)
                            .add("name",name)
                            .add("sex",sex)
                            .add("age",age)
                            .add("classNo",classNo)
                            .add("dept",dept)
                            .add("mobile",mobile)
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
                            onSignupFailed("网络连接失败");
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
                                onSignupFailed(message);
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeDialog();
                                onSignupSuccess(message);
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