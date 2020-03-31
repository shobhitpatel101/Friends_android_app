package com.example.javaapp;

public class Signup_helper {
    public String FullName, Email, DOB, Gender, Phone;

    public Signup_helper(){

    }

    public Signup_helper(String fullName, String email, String DOB, String gender, String phone) {
        this.FullName = fullName;
        this.Email = email;
        this.DOB = DOB;
        this.Gender = gender;
        this.Phone = phone;
    }
}
