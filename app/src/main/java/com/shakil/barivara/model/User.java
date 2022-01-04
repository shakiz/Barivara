package com.shakil.barivara.model;

public class User {
    private String ID;
    private String FirebaseKey;
    private String Name;
    private String UserName;
    private String Email;
    private String DOB;

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
}
