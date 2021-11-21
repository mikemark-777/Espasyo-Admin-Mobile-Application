package com.capstone.espasyoadmin.models;

public class Landlord {
    private String landlordID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int userRole;
    private String phoneNumber;

    public Landlord() {
        //empty landlord constructor **
    }

    public Landlord(String landlordID, String firstName, String lastName, String email, String password, int userRole, String phoneNumber) {
        this.landlordID = landlordID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.phoneNumber = phoneNumber;
    }

    public void setLandlordID(String landlordID) {
        this.landlordID = landlordID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLandlordID() {
        return landlordID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
