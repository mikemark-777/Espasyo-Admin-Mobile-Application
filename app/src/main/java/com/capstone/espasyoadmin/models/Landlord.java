package com.capstone.espasyoadmin.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Landlord implements Parcelable {

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

    protected Landlord(Parcel in) {
        landlordID = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        password = in.readString();
        userRole = in.readInt();
        phoneNumber = in.readString();
    }

    public static final Creator<Landlord> CREATOR = new Creator<Landlord>() {
        @Override
        public Landlord createFromParcel(Parcel in) {
            return new Landlord(in);
        }

        @Override
        public Landlord[] newArray(int size) {
            return new Landlord[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(landlordID);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeInt(userRole);
        dest.writeString(phoneNumber);
    }
}
