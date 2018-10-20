package com.example.slj.gradeadministration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.*;
import org.json.JSONObject;

import java.net.URL;

public class UpdateActivity extends AppCompatActivity {

    private static final String PRE_URL = "http://139.199.25.59:8080/GradeAdministrationApi/grade/";
    private ProgressDialog progressDialog = null;

    @BindView(R.id.update_toolbar)
    Toolbar toolbar;
    @BindView(R.id.input_sno_u) EditText snoText;
    @BindView(R.id.input_name_u) EditText nameText;
    @BindView(R.id.input_sex_u) EditText sexText;
    @BindView(R.id.input_age_u) EditText ageText;
    @BindView(R.id.input_class_u) EditText classText;
    @BindView(R.id.input_dept_u) EditText deptText;
    @BindView(R.id.input_mobile_u) EditText mobileText;
    @BindView(R.id.btn_signup_u) Button signupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
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

        signupButton.setOnClickListener(new View.OnClickListener() {
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

        signupButton.setEnabled(false);

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

        signupButton.setEnabled(true);
    }

    //输入违法
    public boolean validate() {
        boolean valid = true;

        String sno = snoText.getText().toString();
        String name = nameText.getText().toString();
        String sex = sexText.getText().toString();
        String age = ageText.getText().toString();
        String classNo = classText.getText().toString();
        String dept = deptText.getText().toString();
        String mobile = mobileText.getText().toString();

        if (sno.isEmpty() || sno.length() != 10) {
            snoText.setError("学号格式不正确");
        }
        if (name.isEmpty() || name.length() < 2) {
            nameText.setError("姓名格式不正确");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (sex.isEmpty()) {
            sexText.setError("性别不能为空");
            valid = false;
        } else {
            sexText.setError(null);
        }


        if (age.isEmpty() || age.length() > 3) {
            ageText.setError("年龄格式不正确");
            valid = false;
        } else {
            ageText.setError(null);
        }

        if (classNo.isEmpty() || classNo.length() != 8) {
            classText.setError("班级格式不正确");
            valid = false;
        } else {
            classText.setError(null);
        }

        if (dept.isEmpty()) {
            deptText.setError("学院不能为空");
            valid = false;
        } else {
            deptText.setError(null);
        }

        if (mobile.isEmpty() || !Patterns.PHONE.matcher(mobile).matches()) {
            mobileText.setError("请输入正确的手机号码");
            valid = false;
        } else {
            mobileText.setError(null);
        }

        return valid;
    }

    //弹出登录加载框
    public void showDialog(){
        progressDialog = new ProgressDialog(UpdateActivity.this,
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
                String sno = snoText.getText().toString();
                String name = nameText.getText().toString();
                String sex = sexText.getText().toString();
                String age = ageText.getText().toString();
                String classNo = classText.getText().toString();
                String dept = deptText.getText().toString();
                String mobile = mobileText.getText().toString();

                Response response = null;
                try {
                    URL url = new URL(PRE_URL+"updateStudentInfo.json");
                    OkHttpClient httpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("sno",sno)
                            .add("sname",name)
                            .add("ssex",sex)
                            .add("sage",age)
                            .add("sclass",classNo)
                            .add("sdept",dept)
                            .add("stel",mobile)
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
