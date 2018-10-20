package com.example.slj.gradeadministration.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;

import android.widget.ArrayAdapter;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.allen.library.SuperTextView;
import com.example.slj.gradeadministration.GradeActivity;
import com.example.slj.gradeadministration.R;
import com.example.slj.gradeadministration.domain.Sc;
import com.example.slj.gradeadministration.domain.Studentinfo;
import com.example.slj.gradeadministration.utils.ResultGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;


public class UserFragment extends Fragment {
    private static final String PRE_URL = "http://139.199.25.59:8080/GradeAdministrationApi/grade/";
    private static final int EXCEPTION = 0;
    private static final int FAIL = 1;
    private static final int SUCCESS = 2;

    @BindView(R.id.user_toolbar)
    Toolbar userToolbar;
    @BindView(R.id.superTextViewName)
    SuperTextView superTextViewName;
    @BindView(R.id.superTextViewAge)
    SuperTextView superTextViewAge;
    @BindView(R.id.superTextViewSex)
    SuperTextView superTextViewSex;
    @BindView(R.id.superTextViewSno)
    SuperTextView superTextViewSno;
    @BindView(R.id.superTextViewClass)
    SuperTextView superTextViewClass;
    @BindView(R.id.superTextViewDept)
    SuperTextView superTextViewDept;
    @BindView(R.id.superTextViewTel)
    SuperTextView superTextViewTel;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@org.jetbrains.annotations.NotNull Message msg) {
            switch (msg.what){
                case EXCEPTION:
                    Toast.makeText(getContext(),"初始化失败",Toast.LENGTH_LONG);
                    break;
                case FAIL:
                    Toast.makeText(getContext(),msg.obj.toString(),Toast.LENGTH_LONG);
                    break;
                case SUCCESS:
                    Studentinfo studentinfo = (Studentinfo) msg.obj;
                    if (studentinfo == null){
                        Toast.makeText(getContext(),"暂无数据。初始化失败",Toast.LENGTH_LONG);
                        break;
                    }
                    superTextViewName.setRightString(studentinfo.getSname());
                    superTextViewSex.setRightString(studentinfo.getSsex());
                    superTextViewAge.setRightString(studentinfo.getSage());
                    superTextViewSno.setRightString(studentinfo.getSno());
                    superTextViewClass.setRightString(studentinfo.getSclass());
                    superTextViewDept.setRightString(studentinfo.getSdept());
                    superTextViewTel.setRightString(studentinfo.getStel());
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(userToolbar);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this,view);
        initData();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        /*inflater.inflate(R.menu.no_back_toolbar, menu);*/
    }

    public void initData(){
        final String sno = ((GradeActivity)getActivity()).getSNO();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    URL url = new URL(PRE_URL+"getStudentInfo.json");
                    OkHttpClient httpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("sno",sno)
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
                    Type studentInfoType = new TypeToken<ResultGson<Studentinfo>>(){}.getType();
                    ResultGson<Studentinfo> resultGson = gson.fromJson(resultJson,studentInfoType);
                    if (!resultGson.success){
                        Message message = new Message();
                        message.what = FAIL;
                        message.obj = resultGson.message;
                        handler.sendMessage(message);
                        return;
                    }
                    Studentinfo studentinfo = resultGson.object;
                    Message message = new Message();
                    message.what = SUCCESS;
                    message.obj = studentinfo;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
