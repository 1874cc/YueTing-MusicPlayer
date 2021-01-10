package com.liangyi.yueting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    TextView name;
    TextView number;
    TextView sex;
    TextView email;
    Button sign_out;
    User user;
    private static final int UPDATE_INFO=1;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case UPDATE_INFO:
                    name.setText(user.getUserName());
                    number.setText(user.getUserNumber());
                    sex.setText(user.getUserSex());
                    email.setText(user.getUserEmail());
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_activity);
        //初始化layout
        name=findViewById(R.id.userName);
        number=findViewById(R.id.userNumber);
        sex=findViewById(R.id.userSex);
        email=findViewById(R.id.userEmail);
        sign_out=findViewById(R.id.sign_out);
        sign_out.setOnClickListener(this);
        loadUserInfo();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_out:
                user.setUserStatus("0");
                user.save();
                finish();
            default:
                break;
        }
    }

    private void loadUserInfo(){
        Bundle getBundle = this.getIntent().getExtras();
        String nowNumber = "";
        //获取bundle中所有key的值
        Set<String> getKey = getBundle.keySet();
        for (String key : getKey) {
            if ("number".equals(key)) {
                nowNumber = getBundle.getString("number");   //获取用户手机号number
            }
        }
        //根据numebr查找当前用户
        List<User> findUser= LitePal.where("userNumber=?",nowNumber)
                .find(User.class);
        user=findUser.get(0);

        //主线程更新UI
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message=new Message();
                message.what=UPDATE_INFO;
                handler.sendMessage(message);
            }
        }).start();
    }
}