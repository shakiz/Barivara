package com.shakil.barivara.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String ID;
    private String FirebaseKey;
    private String Name;
    private String UserName;
    private String Mobile;
    private String Email;
    private String DOB;

    public User(String ID, String firebaseKey, String name, String userName, String mobile, String email, String DOB) {
        this.ID = ID;
        FirebaseKey = firebaseKey;
        Name = name;
        UserName = userName;
        Mobile = mobile;
        Email = email;
        this.DOB = DOB;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFirebaseKey() {
        return FirebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        FirebaseKey = firebaseKey;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", Name);
        result.put("userName", UserName);
        result.put("email", Email);
        result.put("mobile", Mobile);
        result.put("dOB", DOB);

        return result;
    }
}
