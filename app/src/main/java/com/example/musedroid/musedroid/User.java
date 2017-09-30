package com.example.musedroid.musedroid;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by gdev on 30/9/2017.
 */
@IgnoreExtraProperties
public class User {
    public String name;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public static class Favorites {
        String name;

        public Favorites(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
