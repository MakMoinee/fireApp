package com.expert.fire;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.expert.fire.LocalPreference.LanguagePref;

public class HomeActivity extends AppCompatActivity {

    LinearLayout linearHome, linearPantry, linearUpload, linearUser, linearFavorites;
    TextView lblUser, lblFavorites;
    Boolean isLangEng = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        initListener();
    }

    private void initListener() {
        linearPantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, Pantry.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this).toBundle());
                finish();
            }
        });
        linearUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, Upload.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this).toBundle());

            }
        });
        linearUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, User.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this).toBundle());

            }
        });
        linearFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, Favorites.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this).toBundle());

            }
        });

    }

    private void initViews() {
        linearHome = findViewById(R.id.linearHome);
        lblUser = findViewById(R.id.lblUser);
        linearPantry = findViewById(R.id.linearPantry);
        linearUpload = findViewById(R.id.linearUpload);
        linearUser = findViewById(R.id.linearUser);
        linearFavorites = findViewById(R.id.linearFavorites);
        lblFavorites = findViewById(R.id.lblFavorites);
        isLangEng = new LanguagePref(HomeActivity.this).getIsEng();
        if (isLangEng) {
            lblUser.setText("User");
            lblFavorites.setText("Favorites");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLangEng = new LanguagePref(HomeActivity.this).getIsEng();
        if (isLangEng) {
            lblUser.setText("User");
            lblFavorites.setText("Favorites");
        } else {
            lblUser.setText("Gumagamit");
            lblFavorites.setText("Mga Paborito");
        }
    }
}
