package com.expert.fire;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    LinearLayout linearHome, linearPantry, linearUpload, linearUser, linearFavorites;

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
        linearPantry = findViewById(R.id.linearPantry);
        linearUpload = findViewById(R.id.linearUpload);
        linearUser = findViewById(R.id.linearUser);
        linearFavorites = findViewById(R.id.linearFavorites);
    }
}
