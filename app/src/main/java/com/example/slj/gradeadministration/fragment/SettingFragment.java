package com.example.slj.gradeadministration.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.*;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.allen.library.SuperButton;
import com.allen.library.SuperTextView;
import com.example.slj.gradeadministration.*;
import okhttp3.*;
import org.json.JSONObject;

import java.net.URL;

public class SettingFragment extends Fragment {
    private static final String PRE_URL = "http://139.199.25.59:8080/GradeAdministrationApi/grade/";
    private ProgressDialog progressDialog = null;

    @BindView(R.id.setting_toolbar)
    Toolbar settingToolbar;
    @BindView(R.id.superTextViewUpdate)
    SuperTextView superTextViewUpdate;
    @BindView(R.id.superTextViewSecurity)
    SuperTextView superTextViewSecurity;
    @BindView(R.id.btn_logout)
    SuperButton appCompatButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(settingToolbar);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this,view);
        superTextViewUpdate.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                Intent intent = new Intent(getActivity(), UpdateActivity.class);
                startActivity(intent);
            }
        });
        superTextViewSecurity.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                String sno = getActivity().getIntent().getStringExtra("sno");
                Intent intent = new Intent(getActivity(), SecurityActivity.class);
                intent.putExtra("sno",sno);
                startActivity(intent);
            }
        });
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        /*inflater.inflate(R.menu.no_back_toolbar, menu);*/
    }

    public void logout(){
        final String sno = getActivity().getIntent().getStringExtra("sno");
        showDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    URL url = new URL(PRE_URL+"getLogout.json");
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeDialog();
                            onLogoutFailed("网络连接失败");
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeDialog();
                                onLogoutFailed(message);
                            }
                        });
                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeDialog();
                                onLogoutSuccess();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //注销成功
    public void onLogoutSuccess() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }, 1500);

    }

    //注销失败
    public void onLogoutFailed(String fail_message) {
        Toast.makeText(getContext(), fail_message, Toast.LENGTH_SHORT).show();

        appCompatButton.setEnabled(true);
    }

    //弹出注销加载框
    public void showDialog(){
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("正在注销...");
        progressDialog.show();
    }

    //关闭注销加载框
    public void closeDialog(){
        getActivity().runOnUiThread(new Runnable() {
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
}
