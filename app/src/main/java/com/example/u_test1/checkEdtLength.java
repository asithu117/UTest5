package com.example.u_test1;

public class checkEdtLength {
    public boolean checkName( String fName,String sName){
        if (fName.trim().length()<2 && sName.trim().length()<2){
            return false;
        }
        else {
            return true;
        }

    }
    public boolean checkMail(String Mail){
        if (Mail.trim().length()<15){
            return false;
        }
        else {
            return true;
        }
    }
    public boolean checkAddress(String Address){
        if (Address.trim().length()<5){
            return false;
        }
        else {
            return true;
        }
    }
    public boolean checkPassword(String password,String cPassword){
        if (password.trim().length()<8&&cPassword.trim().length()<8){
            return false;
        }
        else {
            return true;
        }
    }
    public boolean checkPhoneNumgreater(String phone){
        if (phone.trim().length()>6){
            return true;
        }
        else{
            return false;
        }
    }
    public boolean checkPhoneNumless(String phone){
        if (phone.trim().length()<10){
            return true;
        }
        else{
            return false;
        }
    }
    public boolean checkCode(String code){
        if (code.isEmpty() || code.length()<6){
           return false;
        }
        else {
            return true;
        }
    }
}
