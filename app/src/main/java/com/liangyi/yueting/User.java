package com.liangyi.yueting;

import android.app.Application;

import androidx.annotation.NonNull;

import org.litepal.crud.LitePalSupport;
import org.litepal.exceptions.DataSupportException;

public class User extends LitePalSupport {
    private String userName;
    private String userNumber;
    private String userPassword;
    private String userSex;
    private String userStatus;
    private String userEmail;
    public User() {

    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public String getUserPassword() {
        return userPassword;
    }


    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

//    public User(){
//        super();
//    }
}
