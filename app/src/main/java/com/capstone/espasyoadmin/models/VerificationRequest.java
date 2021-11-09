package com.capstone.espasyoadmin.models;

import android.os.Parcel;
import android.os.Parcelable;

public class VerificationRequest implements Parcelable {

    private String verificationRequestID;
    private boolean isVerified;
    private String dateSubmitted;
    private String dateVerified;
    private String declinedVerificationDescription;
    private String requesteeID;
    private String propertyID;
    private String propertyName;
    private String propertyAddress;
    private String barangayBusinessPermitImageURL;
    private String municipalBusinessPermitImageURL;

    public VerificationRequest() {
        //empty verification request constructor **required
    }

    protected VerificationRequest(Parcel in) {
        verificationRequestID = in.readString();
        isVerified = in.readByte() != 0;
        dateSubmitted = in.readString();
        dateVerified = in.readString();
        declinedVerificationDescription = in.readString();
        requesteeID = in.readString();
        propertyID = in.readString();
        propertyName = in.readString();
        propertyAddress = in.readString();
        barangayBusinessPermitImageURL = in.readString();
        municipalBusinessPermitImageURL = in.readString();
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

    public void setVerified(boolean verified) {
        isVerified = verified;
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

    public void setBarangayBusinessPermitImageURL(String barangayBusinessPermitImageURL) {
        this.barangayBusinessPermitImageURL = barangayBusinessPermitImageURL;
    }

    public void setMunicipalBusinessPermitImageURL(String municipalBusinessPermitImageURL) {
        this.municipalBusinessPermitImageURL = municipalBusinessPermitImageURL;
    }

    public String getVerificationRequestID() {
        return verificationRequestID;
    }


    public boolean isVerified() {
        return isVerified;
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

    public String getBarangayBusinessPermitImageURL() {
        return barangayBusinessPermitImageURL;
    }

    public String getMunicipalBusinessPermitImageURL() {
        return municipalBusinessPermitImageURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(verificationRequestID);
        dest.writeByte((byte) (isVerified ? 1 : 0));
        dest.writeString(dateSubmitted);
        dest.writeString(dateVerified);
        dest.writeString(declinedVerificationDescription);
        dest.writeString(requesteeID);
        dest.writeString(propertyID);
        dest.writeString(propertyName);
        dest.writeString(propertyAddress);
        dest.writeString(barangayBusinessPermitImageURL);
        dest.writeString(municipalBusinessPermitImageURL);
    }
}
