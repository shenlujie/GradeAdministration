package com.example.slj.gradeadministration.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.allen.library.SuperTextView;
import com.example.slj.gradeadministration.GradeActivity;
import com.example.slj.gradeadministration.GradeDetailsActivity;
import com.example.slj.gradeadministration.R;
import com.example.slj.gradeadministration.domain.Course;
import com.example.slj.gradeadministration.utils.ResultGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;


public class GradeFragment extends Fragment {
    private static final String PRE_URL = "http://139.199.25.59:8080/GradeAdministrationApi/grade/";
    private static final int EXCEPTION = 0;
    private static final int FAIL = 1;
    private static final int SUCCESS = 2;
    private List<Course> courseList;
    @BindView(R.id.grade_toolbar)
    Toolbar gradeToolbar;
    @BindView(R.id.grade_linearLayout)
    LinearLayout linearLayout;
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
                    courseList = (List<Course>)msg.obj;
                    if (courseList.size() == 0){
                        Toast.makeText(getContext(),"暂无数据，初始化失败",Toast.LENGTH_LONG);
                        break;
                    }
                    System.out.print(courseList.get(0).getCno());
                    for (int i = 0; i < courseList.size(); i++) {
                        SuperTextView superTextView = new SuperTextView(getContext());
                        final int item = i;
                        //修改图片大小
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.course);
                        Bitmap newBmp = Bitmap.createScaledBitmap(bitmap
                                ,(int)(35*getResources().getDisplayMetrics().density+0.5f)
                                , (int)(30*getResources().getDisplayMetrics().density+0.5f)
                                , true);
                        //设置layout参数
                        RelativeLayout.LayoutParams layoutParams
                                = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                                ,(int)(60*getResources().getDisplayMetrics().density+0.5f));
                        superTextView.setLeftIcon(new BitmapDrawable(getResources(), newBmp));
                        superTextView.setLeftString(courseList.get(i).getCname());
                        superTextView.setRightString(">");
                        superTextView.setLayoutParams(layoutParams);
                        superTextView.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
                            @Override
                            public void onClickListener(SuperTextView superTextView) {
                                final String sno = ((GradeActivity)getActivity()).getSNO();
                                Intent intent = new Intent(getActivity(), GradeDetailsActivity.class);
                                intent.putExtra("cno",courseList.get(item).getCno());
                                intent.putExtra("sno",sno);
                                startActivity(intent);
                            }
                        });
                        linearLayout.addView(superTextView);
                    }
                    break;

            }
        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(gradeToolbar);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grade, container, false);
        ButterKnife.bind(this,view);
        initData();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
//        inflater.inflate(R.menu.menu_parent_fragment, menu);
    }

    public void initData(){
        final String sno = ((GradeActivity)getActivity()).getSNO();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    URL url = new URL(PRE_URL+"getCourseInfo.json");
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
                    Type courseType = new TypeToken<ResultGson< List<Course> >>(){}.getType();
                    ResultGson<List<Course>> resultGson = gson.fromJson(resultJson,courseType);
                    if (!resultGson.success){
                        Message message = new Message();
                        message.what = FAIL;
                        message.obj = resultGson.message;
                        handler.sendMessage(message);
                        return;
                    }
                    List<Course> courseList = resultGson.object;
                    System.out.print(courseList.get(0).getCno());
                    Message message = new Message();
                    message.what = SUCCESS;
                    message.obj = courseList;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
