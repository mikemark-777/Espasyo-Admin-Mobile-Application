package com.capstone.espasyoadmin.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Property implements Parcelable{

    private String propertyID;
    private String owner;
    private double latitude;
    private double longitude;
    private boolean isVerified;
    private boolean isLocked;
    private String verificationID;
    private String propertyType;
    private String name;
    private String address;
    private String proprietorName;
    private int minimumPrice;
    private int maximumPrice;
    private boolean isElectricityIncluded;
    private boolean isWaterIncluded;
    private boolean isInternetIncluded;
    private boolean isGarbageCollectionIncluded;

    public Property(){
        //empty property constructor **required`
    }

/*    public Property(String propertyID, String owner, double latitude, double longitude, boolean isVerified, boolean isLocked, String verificationID, String propertyType, String name, String address,
                    String proprietorName, String landlordName, String landlordPhoneNumber, int minimumPrice, int maximumPrice, boolean isElectricityIncluded, boolean isWaterIncluded, boolean isInternetIncluded, boolean isGarbageCollectionIncluded) {
        this.propertyID = propertyID;
        this.owner = owner;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isVerified = isVerified;
        this.isLocked = isLocked;
        this.verificationID = verificationID;
        this.propertyType = propertyType;
        this.name = name;
        this.address = address;
        this.proprietorName = proprietorName;
        this.landlordName = landlordName;
        this.landlordPhoneNumber = landlordPhoneNumber;
        this.minimumPrice = minimumPrice;
        this.maximumPrice = maximumPrice;
        this.isElectricityIncluded = isElectricityIncluded;
        this.isWaterIncluded = isWaterIncluded;
        this.isInternetIncluded = isInternetIncluded;
        this.isGarbageCollectionIncluded = isGarbageCollectionIncluded;
    }*/

    //setters

    protected Property(Parcel in) {
        propertyID = in.readString();
        owner = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        isVerified = in.readByte() != 0;
        isLocked = in.readByte() != 0;
        verificationID = in.readString();
        propertyType = in.readString();
        name = in.readString();
        address = in.readString();
        proprietorName = in.readString();
        minimumPrice = in.readInt();
        maximumPrice = in.readInt();
        isElectricityIncluded = in.readByte() != 0;
        isWaterIncluded = in.readByte() != 0;
        isInternetIncluded = in.readByte() != 0;
        isGarbageCollectionIncluded = in.readByte() != 0;
    }

    public static final Creator<Property> CREATOR = new Creator<Property>() {
        @Override
        public Property createFromParcel(Parcel in) {
            return new Property(in);
        }

        @Override
        public Property[] newArray(int size) {
            return new Property[size];
        }
    };

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setIsVerified(boolean verified) {
        isVerified = verified;
    }

    public void setIsLocked(boolean locked) {
        isLocked = locked;
    }

    public void setVerificationID(String verificationID) {
        this.verificationID = verificationID;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProprietorName(String proprietorName) {
        this.proprietorName = proprietorName;
    }

    public void setMinimumPrice(int minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public void setMaximumPrice(int maximumPrice) {
        this.maximumPrice = maximumPrice;
    }

    public void setIsElectricityIncluded(boolean electricityIncluded) {
        isElectricityIncluded = electricityIncluded;
    }

    public void setIsWaterIncluded(boolean waterIncluded) {
        isWaterIncluded = waterIncluded;
    }

    public void setIsInternetIncluded(boolean internetIncluded) {
        isInternetIncluded = internetIncluded;
    }

    public void setIsGarbageCollectionIncluded(boolean garbageCollectionIncluded) {
        isGarbageCollectionIncluded = garbageCollectionIncluded;
    }

    //getters

    public String getPropertyID() { return propertyID; }

    public String getOwner() {
        return owner;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean getIsVerified() {
        return isVerified;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public String getVerificationID() {
        return verificationID;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getProprietorName() {
        return proprietorName;
    }

    public int getMinimumPrice() {
        return minimumPrice;
    }

    public int getMaximumPrice() {
        return maximumPrice;
    }

    public boolean getIsElectricityIncluded() {
        return isElectricityIncluded;
    }

    public boolean getIsWaterIncluded() {
        return isWaterIncluded;
    }

    public boolean getIsInternetIncluded() {
        return isInternetIncluded;
    }

    public boolean getIsGarbageCollectionIncluded() {
        return isGarbageCollectionIncluded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(propertyID);
        dest.writeString(owner);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeByte((byte) (isVerified ? 1 : 0));
        dest.writeByte((byte) (isLocked ? 1 : 0));
        dest.writeString(verificationID);
        dest.writeString(propertyType);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(proprietorName);
        dest.writeInt(minimumPrice);
        dest.writeInt(maximumPrice);
        dest.writeByte((byte) (isElectricityIncluded ? 1 : 0));
        dest.writeByte((byte) (isWaterIncluded ? 1 : 0));
        dest.writeByte((byte) (isInternetIncluded ? 1 : 0));
        dest.writeByte((byte) (isGarbageCollectionIncluded ? 1 : 0));
    }
}
