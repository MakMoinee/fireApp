package com.expert.fire.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.expert.fire.LocalPreference.LanguagePref;
import com.expert.fire.R;

import java.util.List;

public class LanguageAdapter extends BaseAdapter {

    Context mContext;
    List<String> languages;
    Boolean isEng;

    public LanguageAdapter(Context mContext, List<String> languages) {
        this.mContext = mContext;
        this.languages = languages;
        this.isEng = new LanguagePref(this.mContext).getIsEng();
    }

    @Override
    public int getCount() {
        return languages.size();
    }

    @Override
    public Object getItem(int position) {
        return languages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.list_item_lang, parent, false);
        TextView txtLang = mView.findViewById(R.id.txtLanguage);
        ImageView imgCircle = mView.findViewById(R.id.imgCircle);
        String lang = languages.get(position);
        txtLang.setText(lang);
        if (isEng) {
            if (lang.equalsIgnoreCase("english")) {
                imgCircle.setVisibility(View.VISIBLE);
            }
        } else {
            if (lang.equalsIgnoreCase("tagalog")) {
                imgCircle.setVisibility(View.VISIBLE);
            }
        }
        return mView;
    }
}
