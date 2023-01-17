package com.expert.fire.LocalPreference;

import android.content.Context;
import android.content.SharedPreferences;

import com.expert.fire.Models.PersonalInfo;

public class PersonalInfoPref {
    Context mContext;
    SharedPreferences pref;

    public PersonalInfoPref(Context c) {
        this.mContext = c;
        this.pref = this.mContext.getSharedPreferences("personalInfo", Context.MODE_PRIVATE);
    }

    public void storeInfo(PersonalInfo personalInfo) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("docID", personalInfo.getDocID());
        editor.putString("name", personalInfo.getName());
        editor.putString("email", personalInfo.getEmail());
        editor.putString("password", personalInfo.getPassword());
        editor.putString("phoneNum", personalInfo.getPhoneNum());
        editor.commit();
        editor.apply();
    }

    public PersonalInfo getInfo() {
        PersonalInfo info = new PersonalInfo();
        info.setName(pref.getString("name", ""));
        info.setPassword(pref.getString("password", ""));
        info.setEmail(pref.getString("email", ""));
        info.setPhoneNum(pref.getString("phoneNum", ""));
        info.setDocID(pref.getString("docID", ""));

        if (info.getName().equals("")) {
            return null;
        }

        return info;
    }
}
