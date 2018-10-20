package com.example.slj.gradeadministration;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.slj.gradeadministration.domain.Sc;
import com.example.slj.gradeadministration.fragment.GradeFragment;
import com.example.slj.gradeadministration.fragment.SettingFragment;
import com.example.slj.gradeadministration.fragment.UserFragment;
import com.example.slj.gradeadministration.utils.ResultGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

public class GradeActivity extends AppCompatActivity {

    private FragmentTransaction fragmentTransaction;//fragment事务
    //bottom navigation item监听器
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_grade:
                    showNav(R.id.navigation_grade);
                    return true;
                case R.id.navigation_user:
                    showNav(R.id.navigation_user);
                    return true;
                case R.id.navigation_setting:
                    showNav(R.id.navigation_setting);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        showNav(R.id.navigation_grade);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    //按下返回键
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("确定要退出吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    //根据监听器展示相应的fragment
    private void showNav(int navId){

        switch (navId){
            case R.id.navigation_grade:
                GradeFragment gradeFragment = new GradeFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_content,gradeFragment,"fragmentName");
                fragmentTransaction.commit();
                break;
            case R.id.navigation_user:
                UserFragment userFragment = new UserFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_content,userFragment,"fragmentName");
                fragmentTransaction.commit();
                break;
            case R.id.navigation_setting:
                SettingFragment settingFragment = new SettingFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_content,settingFragment,"fragmentName");
                fragmentTransaction.commit();
                break;
        }
    }

    public String getSNO(){
        String SNO = getIntent().getStringExtra("sno");//获取登录进来的学号信息
        return SNO;
    }

}
