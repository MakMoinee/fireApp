package com.expert.fire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.expert.fire.Adapters.FavoritesAdapter;
import com.expert.fire.Interfaces.SimpleListener;
import com.expert.fire.LocalPreference.FavoritesPref;
import com.expert.fire.LocalPreference.LanguagePref;
import com.expert.fire.Models.Dishes;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class Favorites extends AppCompatActivity {

    ListView lv;
    MaterialToolbar toolbar;
    FavoritesAdapter adapter;
    List<Dishes> dishList;
    TextView txtNoFavorites;
    Boolean isLangEng = false;
    ImageButton btnPantry, btnUser, btnFavorites, btnUpload;
    TextView lblUser, lblFavorites;

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
        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Favorites.this, User.class));
                finish();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Favorites.this, Upload.class));
                finish();
            }
        });
        btnPantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Favorites.this, Pantry.class));
                finish();
            }
        });
    }

    private void initViews() {
        lv = findViewById(R.id.lv);
        toolbar = findViewById(R.id.mToolbar);
        txtNoFavorites = findViewById(R.id.txtNoFavorites);
        txtNoFavorites.setVisibility(View.GONE);
        btnPantry = findViewById(R.id.btnPantry);
        btnUser = findViewById(R.id.btnUser);
        btnFavorites = findViewById(R.id.btnFavorites);
        btnUpload = findViewById(R.id.btnUpload);
        lblUser = findViewById(R.id.lblUser);
        lblFavorites = findViewById(R.id.lblFavorites);
        isLangEng = new LanguagePref(Favorites.this).getIsEng();
        if (isLangEng) {
            txtNoFavorites.setText("There are no list of favorites");
            toolbar.setTitle("Favorites");
            lblUser.setText("User");
            lblFavorites.setText("Favorites");
        }
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