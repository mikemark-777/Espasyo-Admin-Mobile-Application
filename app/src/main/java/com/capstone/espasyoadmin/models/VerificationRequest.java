package com.capstone.espasyoadmin.models;

import android.os.Parcel;
import android.os.Parcelable;

public class VerificationRequest implements Parcelable {

    private String verificationRequestID;
    private boolean isExpired;
    private String status;
    private String classification;
    private String dateSubmitted;
    private String dateVerified;
    private String declinedVerificationDescription;
    private String requesteeID;
    private String propertyID;
    private String propertyName;
    private String propertyAddress;
    private String municipalBusinessPermitImageURL;

    public VerificationRequest() {
        //empty verification request constructor **required
    }

    //setters


    protected VerificationRequest(Parcel in) {
        verificationRequestID = in.readString();
        isExpired = in.readByte() != 0;
        status = in.readString();
        classification = in.readString();
        dateSubmitted = in.readString();
        dateVerified = in.readString();
        declinedVerificationDescription = in.readString();
        requesteeID = in.readString();
        propertyID = in.readString();
        propertyName = in.readString();
        propertyAddress = in.readString();
        municipalBusinessPermitImageURL = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(verificationRequestID);
        dest.writeByte((byte) (isExpired ? 1 : 0));
        dest.writeString(status);
        dest.writeString(classification);
        dest.writeString(dateSubmitted);
        dest.writeString(dateVerified);
        dest.writeString(declinedVerificationDescription);
        dest.writeString(requesteeID);
        dest.writeString(propertyID);
        dest.writeString(propertyName);
        dest.writeString(propertyAddress);
        dest.writeString(municipalBusinessPermitImageURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VerificationRequest> CREATOR = new Creator<VerificationRequest>() {
        @Override
        public VerificationRequest createFromParcel(Parcel in) {
            return new VerificationRequest(in);
        }

        @Override
        public VerificationRequest[] newArray(int size) {
            return new VerificationRequest[size];
        }
    };

    public void setVerificationRequestID(String verificationRequestID) {
        this.verificationRequestID = verificationRequestID;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public void setDateSubmitted(String dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public void setDateVerified(String dateVerified) {
        this.dateVerified = dateVerified;
    }

    public void setDeclinedVerificationDescription(String declinedVerificationDescription) {
        this.declinedVerificationDescription = declinedVerificationDescription;
    }

    public void setRequesteeID(String requesteeID) {
        this.requesteeID = requesteeID;
    }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public void setMunicipalBusinessPermitImageURL(String municipalBusinessPermitImageURL) {
        this.municipalBusinessPermitImageURL = municipalBusinessPermitImageURL;
    }

    //getters

    public String getVerificationRequestID() {
        return verificationRequestID;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public String getStatus() {
        return status;
    }

    public String getClassification() {
        return classification;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public String getDateVerified() {
        return dateVerified;
    }

    public String getDeclinedVerificationDescription() {
        return declinedVerificationDescription;
    }

    public String getRequesteeID() {
        return requesteeID;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public String getMunicipalBusinessPermitImageURL() {
        return municipalBusinessPermitImageURL;
    }
}
