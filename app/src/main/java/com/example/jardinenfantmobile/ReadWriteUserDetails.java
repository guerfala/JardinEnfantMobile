package com.example.jardinenfantmobile;

public class ReadWriteUserDetails {
    public String doB, gender, mobile;

    public ReadWriteUserDetails(){};
    public ReadWriteUserDetails(String textdoB, String textGender, String textMobile){
        this.doB = textdoB;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
