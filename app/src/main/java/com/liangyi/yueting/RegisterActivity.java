package com.liangyi.yueting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText name;
    private EditText password;
    private EditText password2;
    private EditText email;
    private EditText number;
    private EditText sex;

    private String inputName;
    private String inputNumber;
    private String inputPassword;
    private String inputPassword2;
    private String inputSex;
    private String inputEmail;

    private Button register_btn;
    private TextView login_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        //加载布局
        name=findViewById(R.id.Name);
        number=findViewById(R.id.Number);
        password=findViewById(R.id.Password);
        password2=findViewById(R.id.Password2);
        email=findViewById(R.id.Email);
        sex=findViewById(R.id.Sex);

        register_btn=findViewById(R.id.register_btn);
        login_text=findViewById(R.id.sign_in);

        //注册按钮
        register_btn.setOnClickListener(this);

        //跳转登录界面
        login_text.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_btn:
                //注册操作
                //判断输入的规范
                getAllInput();
                if(inputNumber.equals("")||inputNumber==null) {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (inputName.equals("")||inputName==null) {
                    Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (inputPassword.equals("")||inputPassword==null) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!inputPassword2.equals(inputPassword)) {
                    Toast.makeText(this, "两次输入密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user=new User();
                user.setUserStatus("0");
                user.setUserName(inputName);
                user.setUserPassword(inputPassword);
                user.setUserEmail(inputEmail);
                user.setUserSex(inputSex);
                user.setUserNumber(inputNumber);
                user.save();
                Toast.makeText(this,"注册成功，返回登录界面",Toast.LENGTH_SHORT).show();
                finish();
                break;

            case R.id.sign_in:
                finish();
                break;

            default:
                break;
        }
    }

    public void getAllInput(){
        //获取所有输入的信息
        //先对手机号和密码判断
        inputNumber=number.getText().toString();
        inputName=name.getText().toString();
        inputPassword=password.getText().toString();
        inputPassword2=password2.getText().toString();
        inputEmail=email.getText().toString();
        inputSex=sex.getText().toString();
    }
}