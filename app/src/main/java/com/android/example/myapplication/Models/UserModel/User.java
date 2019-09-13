package com.android.example.myapplication.Models.UserModel;

import android.net.Uri;

public class User {
    private String id;
    private String email;
    private String displayName;
    private Uri photoUrl;

    public User(String id, String email, String displayName, Uri photoUrl) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }
}
