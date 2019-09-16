package com.android.example.myapplication.Models.UserModel;

import android.net.Uri;

public class UserData {
    private String email;
    private String displayName;
    private String photoUrl;

    public UserData(String email, String displayName, String photoUrl) {
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
