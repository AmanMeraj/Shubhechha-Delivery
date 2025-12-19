package com.subh.shubhechhadelivery.Model;

import java.io.Serializable;

public class LoginRequest implements Serializable {

    public String mobile ;
    public String password;

    public String type ;


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
