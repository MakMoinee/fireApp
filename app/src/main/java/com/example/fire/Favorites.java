package com.example.fire;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fire.Adapters.FavoritesAdapter;
import com.example.fire.Interfaces.SimpleListener;
import com.example.fire.LocalPreference.FavoritesPref;
import com.example.fire.Models.Dishes;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

import java.util.List;

public class Favorites extends AppCompatActivity {

    ListView lv;
    MaterialToolbar toolbar;
    FavoritesAdapter adapter;
    List<Dishes> dishList;
    TextView txtNoFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        initViews();
        initListeners();

    }

    private void initListeners() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        lv = findViewById(R.id.lv);
        toolbar = findViewById(R.id.mToolbar);
        txtNoFavorites = findViewById(R.id.txtNoFavorites);
        txtNoFavorites.setVisibility(View.GONE);
        loadData();

    }

    private void loadData() {
        dishList = new FavoritesPref(Favorites.this).getDishes();
        if (dishList != null) {
            if (dishList.size() == 0) {
                lv.setVisibility(View.GONE);
                txtNoFavorites.setVisibility(View.VISIBLE);
            }
            adapter = new FavoritesAdapter(Favorites.this, dishList, new SimpleListener() {
                @Override
                public void refreshList() {
                    loadData();
                }
            });
            lv.setAdapter(adapter);
        }
    }
}