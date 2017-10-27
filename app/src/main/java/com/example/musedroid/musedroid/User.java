package com.example.musedroid.musedroid;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by gdev on 30/9/2017.
 */
@IgnoreExtraProperties
class User {
    public String name;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
