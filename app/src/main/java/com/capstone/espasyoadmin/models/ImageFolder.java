package com.capstone.espasyoadmin.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ImageFolder implements Parcelable {

    private String folderID;
    private ArrayList<String> images;

    public ImageFolder() {
        //empty constructor
    }

    protected ImageFolder(Parcel in) {
        folderID = in.readString();
        images = in.createStringArrayList();
    }

    public static final Creator<ImageFolder> CREATOR = new Creator<ImageFolder>() {
        @Override
        public ImageFolder createFromParcel(Parcel in) {
            return new ImageFolder(in);
        }

        @Override
        public ImageFolder[] newArray(int size) {
            return new ImageFolder[size];
        }
    };

    public void setFolderID(String folderID) {
        this.folderID = folderID;
    }


    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getFolderID() {
        return folderID;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(folderID);
        dest.writeStringList(images);
    }
}
