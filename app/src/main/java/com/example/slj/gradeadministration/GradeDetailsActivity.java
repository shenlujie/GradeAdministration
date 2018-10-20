package com.example.slj.gradeadministration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.allen.library.SuperTextView;
import com.example.slj.gradeadministration.domain.Course;
import com.example.slj.gradeadministration.domain.GradeInfoDetails;
import com.example.slj.gradeadministration.utils.ResultGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

public class GradeDetailsActivity extends AppCompatActivity {
    private static final String PRE_URL = "http://139.199.25.59:8080/GradeAdministrationApi/grade/";
    private static final int EXCEPTION = 0;
    private static final int FAIL = 1;
    private static final int SUCCESS = 2;
    private GradeInfoDetails gradeInfoDetails;

    @BindView(R.id.grade_details_toolbar)
    Toolbar toolbar;
    @BindView(R.id.superTextView_Sno)
    SuperTextView superTextView_Sno;
    @BindView(R.id.superTextView_Course)
    SuperTextView superTextView_Course;
    @BindView(R.id.superTextView_Teacher)
    SuperTextView superTextView_Teacher;
    @BindView(R.id.superTextView_Grade)
    SuperTextView superTextView_Grade;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@org.jetbrains.annotations.NotNull Message msg) {
            switch (msg.what){
                case EXCEPTION:
                    Toast.makeText(GradeDetailsActivity.this,"初始化失败",Toast.LENGTH_LONG);
                    break;
                case FAIL:
                    Toast.makeText(GradeDetailsActivity.this,msg.obj.toString(),Toast.LENGTH_LONG);
                    break;
                case SUCCESS:
                    gradeInfoDetails = (GradeInfoDetails) msg.obj;
                    if (gradeInfoDetails == null){
                        Toast.makeText(GradeDetailsActivity.this,"暂无数据，初始化失败",Toast.LENGTH_LONG);
                        break;
                    }
                    String sno = getIntent().getStringExtra("sno");
                    superTextView_Sno.setRightString(sno);
                    superTextView_Course.setRightTopString(gradeInfoDetails.getCname());
                    superTextView_Course.setRightBottomString(String.valueOf(gradeInfoDetails.getCno()));
                    superTextView_Teacher.setRightTopString(gradeInfoDetails.getTname());
                    superTextView_Teacher.setRightBottomString(gradeInfoDetails.getTno());
                    superTextView_Grade.setRightString(gradeInfoDetails.getGrade());
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_details);
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
        initData();
    }

    public void initData(){
        final String sno = getIntent().getStringExtra("sno");
        final int cno = getIntent().getIntExtra("cno",0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    URL url = new URL(PRE_URL+"getGradeInfoDetails.json");
                    OkHttpClient httpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("sno",sno)
                            .add("cno",String.valueOf(cno))
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    response = httpClient.newCall(request).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.obj = EXCEPTION;
                    handler.sendMessage(message);
                    return;

                }

                try {
                    String resultJson = response.body().string();
                    System.out.print(resultJson);
                    Gson gson = new Gson();
                    Type gradeDetailsType = new TypeToken<ResultGson<GradeInfoDetails>>(){}.getType();
                    ResultGson<GradeInfoDetails> resultGson = gson.fromJson(resultJson,gradeDetailsType);
                    if (!resultGson.success){
                        Message message = new Message();
                        message.what = FAIL;
                        message.obj = resultGson.message;
                        handler.sendMessage(message);
                        return;
                    }
                    GradeInfoDetails gradeInfoDetails = resultGson.object;
                    Message message = new Message();
                    message.what = SUCCESS;
                    message.obj = gradeInfoDetails;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
