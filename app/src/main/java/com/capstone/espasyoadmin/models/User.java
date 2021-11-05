package com.capstone.espasyoadmin.models;

public class User {

    private String UID;
    private String email;
    private String password;
    private int userRole;

    public User() {
        //empty user constructor
    }

    public User(String UID, String email, String password, int userRole) {
        this.UID = UID;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public String getUID() {
        return UID;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getUserRole() {
        return userRole;
    }
}
