package com.expert.fire.LocalPreference;

import android.content.Context;
import android.content.SharedPreferences;

import com.expert.fire.Models.Users;

public class UserPreferences {

    Context mContext;
    SharedPreferences pref;

    public UserPreferences(Context context) {
        this.mContext = context;
        pref = context.getSharedPreferences("users", Context.MODE_PRIVATE);
    }

    public void saveLogin(Users users) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", users.getEmail());
        editor.putString("password", users.getPassword());
        editor.commit();
        editor.apply();
    }

    public Users getUsers() {
        Users users = new Users();
        String email = pref.getString("email", "");
        String password = pref.getString("password", "");
        if (email.equals("") && password.equals("")) {
            return null;
        }
        users.setEmail(email);
        users.setPassword(password);
        return users;
    }

    public void clearUsers() {
        pref.getAll().clear();
    }
}
