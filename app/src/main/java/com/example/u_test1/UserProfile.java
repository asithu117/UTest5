package com.example.u_test1;

public class UserProfile {
    public String name,email,address,division,phone;
    public UserProfile(){

    }

    public UserProfile(String name, String email, String address, String division, String phone) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.division = division;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
