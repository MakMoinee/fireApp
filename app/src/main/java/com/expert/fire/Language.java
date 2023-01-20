package com.expert.fire;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.expert.fire.Adapters.LanguageAdapter;
import com.expert.fire.LocalPreference.LanguagePref;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class Language extends AppCompatActivity {

    MaterialToolbar toolbar;
    ListView lv;
    LanguageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        initViews();
        initListener();
    }

    private void initListener() {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        new LanguagePref(Language.this).storeIsEng(false);
                        lv.setAdapter(null);
                        loadList();
                        break;
                    case 1:
                        new LanguagePref(Language.this).storeIsEng(true);
                        lv.setAdapter(null);
                        loadList();
                        break;
                }
            }
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        lv = findViewById(R.id.lv);
        loadList();
    }

    private void loadList() {
        List<String> list = new ArrayList<>();
        list.add("Tagalog");
        list.add("English");
        adapter = new LanguageAdapter(Language.this, list);
        lv.setAdapter(adapter);
    }
}