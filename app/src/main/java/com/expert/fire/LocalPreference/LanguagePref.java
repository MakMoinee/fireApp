package com.expert.fire.LocalPreference;

import android.content.Context;
import android.content.SharedPreferences;

public class LanguagePref {
    Context mContext;
    SharedPreferences pref;

    public LanguagePref(Context c) {
        this.mContext = c;
        this.pref = this.mContext.getSharedPreferences("language", Context.MODE_PRIVATE);
    }

    public void storeIsEng(Boolean isEng) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isEng", isEng);
        editor.commit();
        editor.apply();
    }

    public boolean getIsEng() {
        return pref.getBoolean("isEng", false);
    }
}
