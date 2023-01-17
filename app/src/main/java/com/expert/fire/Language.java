package com.expert.fire;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.expert.fire.Adapters.LanguageAdapter;
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
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        lv = findViewById(R.id.lv);
        List<String> list = new ArrayList<>();
        list.add("Tagalog");
        adapter = new LanguageAdapter(Language.this, list);
        lv.setAdapter(adapter);
    }
}