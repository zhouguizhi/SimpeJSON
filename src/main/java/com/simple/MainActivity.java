package com.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity" ;
    private Button btn_cover;
    private Button btn_uncover;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_cover = (Button) findViewById(R.id.btn_cover);
        btn_uncover = (Button) findViewById(R.id.btn_uncover);
        btn_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modleToJson();
            }
        });
        btn_uncover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonToModel();
            }
        });
    }

    private void jsonToModel() {
       List<UserInfo> objects = (List<UserInfo>) SimpleJSON.jsonToList(json,UserInfo.class);
        Log.e(TAG,"objects="+objects);
        for(UserInfo userInfo:objects){
            Log.e(TAG,"userInfo="+userInfo.toString());
        }
    }
    String json;
    private void modleToJson() {
        User user = new User();
        user.setName("zhouguizhi");
        user.setAge(18);
        user.setTime(System.currentTimeMillis());
        user.setMember(true);
        List<UserInfo> infoList = new ArrayList<>();
        for(int i=0;i<5;i++){
            UserInfo userInfo  = new UserInfo();
            userInfo.setId(""+i);
            userInfo.setName("张三"+i);
            userInfo.setProvide("江苏省"+i);
            userInfo.setPwd("123456"+i);
            infoList.add(userInfo);
        }
        user.setInfo(infoList);
//        json =  SimpleJSON.toJson(infoList);
        json =  SimpleJSON.toJson(infoList);
        Log.e(TAG,"json="+json);
    }
}
