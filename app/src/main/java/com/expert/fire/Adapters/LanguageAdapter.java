package com.expert.fire.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.expert.fire.R;

import java.util.List;

public class LanguageAdapter extends BaseAdapter {

    Context mContext;
    List<String> languages;

    public LanguageAdapter(Context mContext, List<String> languages) {
        this.mContext = mContext;
        this.languages = languages;
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
        String lang = languages.get(position);
        txtLang.setText(lang);
        return mView;
    }
}
