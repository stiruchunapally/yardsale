package com.sreekar.yardsale.models;

import com.google.firebase.database.IgnoreExtraProperties;

/*
This is the basic layout for the user object.
 */

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
